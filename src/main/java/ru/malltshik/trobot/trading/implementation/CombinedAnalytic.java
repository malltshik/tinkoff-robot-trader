package ru.malltshik.trobot.trading.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.malltshik.trobot.properties.TinkoffProps;
import ru.malltshik.trobot.trading.implementation.data.AnalyticReport;
import ru.malltshik.trobot.trading.implementation.data.AnalyticReport.Forecast;
import ru.tinkoff.invest.openapi.OpenApi;
import ru.tinkoff.invest.openapi.models.market.CandleInterval;
import ru.tinkoff.invest.openapi.models.market.Orderbook;
import ru.tinkoff.invest.openapi.models.market.TradeStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static io.micrometer.core.instrument.util.TimeUtils.convert;
import static java.lang.Double.doubleToLongBits;
import static java.math.BigDecimal.ROUND_HALF_UP;
import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static ru.malltshik.trobot.trading.implementation.calc.OrderbookCalc.calcFlatQuantities;

@Slf4j
@Service
@RequiredArgsConstructor
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CombinedAnalytic {

    private final static BigDecimal HUNDRED = BigDecimal.valueOf(100);

    @Autowired
    private OpenApi api;
    @Autowired
    private TinkoffProps tinkoffProps;

    private boolean active = false;
    private String figi = null;
    private Consumer<AnalyticReport> reportRecipient = null;
    private BigDecimal dayTradingValue = ZERO;
    private BigDecimal minTradingValue = ZERO;

    public void start(@NotNull String figi, @NotNull Consumer<AnalyticReport> consumer) {
        Objects.requireNonNull(figi);
        Objects.requireNonNull(consumer);
        this.figi = figi;
        this.reportRecipient = consumer;
        this.active = true;
        pullOrderbook();
        pullCandles();
    }

    public void stop() {
        this.figi = null;
        this.reportRecipient = null;
        this.active = false;
        pullOrderbook();
        pullCandles();
    }

    @Scheduled(fixedDelay = 5000)
    private void pullOrderbook() {
        if (!active) return;
        log.info("Scheduled new pull orderbook operation for {}", figi);
        api.getMarketContext().getMarketOrderbook(figi, 20).thenAccept(opt -> opt.ifPresent(this::orderbookProcess));
    }

    @Scheduled(fixedDelay = 1000 * 60)
    private void pullCandles() {
        if (!active) return;
        log.info("Scheduled new pull candles operation for {}", figi);
        OffsetDateTime start = LocalDate.now().atStartOfDay().atOffset(OffsetDateTime.now().getOffset());
        OffsetDateTime end = start.plusDays(1);
        api.getMarketContext().getMarketCandles(figi, start, end, CandleInterval.ONE_MIN).thenAccept(opt ->
                opt.ifPresent(hc -> {
                    dayTradingValue = hc.candles.stream().map(c -> c.tradesValue).reduce(ZERO, BigDecimal::add);
                    minTradingValue = dayTradingValue.divide(BigDecimal.valueOf(hc.candles.size()), ROUND_HALF_UP);
                }));
    }

    private void orderbookProcess(@NotNull Orderbook ob) {
        Objects.requireNonNull(ob);
        if (ob.lastPrice == null) {
            log.warn("Orderbook last price is null for {}", figi);
            return;
        }
        if (ob.tradeStatus.equals(TradeStatus.NotAvailableForTrading)) {
            log.info("NotAvailableForTrading status for {}", figi);
            return;
        }
        ZonedDateTime now = ZonedDateTime.now();
        ArrayList<Forecast> forecasts = getForecasts(ob, now);
        Forecast bestForecast = forecasts.stream().max(Comparator.comparing(Forecast::getProfit)).orElse(null);
        AnalyticReport report = AnalyticReport.builder()
                .lastPrice(ob.lastPrice).created(now).forecasts(forecasts).bestForecast(bestForecast)
                .build();
        reportRecipient.accept(report);
    }

    @NotNull
    private ArrayList<Forecast> getForecasts(@NotNull Orderbook ob, @NotNull ZonedDateTime now) {
        Objects.requireNonNull(ob.lastPrice);
        List<BigDecimal> quantities = calcFlatQuantities(ob);
        int depth = quantities.size();
        ArrayList<Forecast> forecasts = new ArrayList<>();
        for (int i = 0; i < depth; i++) {
            BigDecimal quantity = quantities.get(i);
            if (quantity.compareTo(ZERO) == 0) continue;
            // calc time forecast
            BigDecimal futurePrice = ob.lastPrice.add(BigDecimal.valueOf(i));
            BigDecimal tradingValue = futurePrice.multiply(quantity).abs();
            double min = tradingValue.divide(minTradingValue, ROUND_HALF_UP).doubleValue();
            double millis = convert(min, TimeUnit.MINUTES, TimeUnit.MILLISECONDS);
            ZonedDateTime to = now.plus(doubleToLongBits(millis), ChronoUnit.MILLIS);
            // calc operation profits
            BigDecimal revenue = ob.minPriceIncrement.multiply(BigDecimal.valueOf(i));
            BigDecimal countryTax = revenue.divide(valueOf(tinkoffProps.getCountryTax() * 100), ROUND_HALF_UP);
            BigDecimal operationValue = ob.lastPrice.multiply(valueOf(2)).add(revenue);
            BigDecimal brokerTax = operationValue.divide(valueOf(tinkoffProps.getBrokerTax() * 100), ROUND_HALF_UP);
            BigDecimal taxes = countryTax.add(brokerTax);
            BigDecimal profit = revenue.subtract(taxes);
            BigDecimal profitPercents = profit.divide(ob.lastPrice.divide(HUNDRED, ROUND_HALF_UP), ROUND_HALF_UP);
            // build forecast
            Forecast forecast = Forecast.builder()
                    .from(now)
                    .to(to)
                    .direction(quantity.compareTo(ZERO))
                    .points(i)
                    .operationValue(operationValue)
                    .revenue(revenue)
                    .taxes(taxes)
                    .profit(profit)
                    .profitPercents(profitPercents)
                    .build();
            forecasts.add(forecast);
        }
        return forecasts;
    }
}
