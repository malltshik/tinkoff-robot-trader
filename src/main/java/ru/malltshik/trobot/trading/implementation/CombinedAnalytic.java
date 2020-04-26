package ru.malltshik.trobot.trading.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.malltshik.trobot.listeners.events.NextCandleEvent;
import ru.malltshik.trobot.properties.TinkoffProps;
import ru.malltshik.trobot.trading.Trader;
import ru.malltshik.trobot.trading.implementation.data.AnalyticReport;
import ru.tinkoff.invest.openapi.OpenApi;
import ru.tinkoff.invest.openapi.models.market.CandleInterval;
import ru.tinkoff.invest.openapi.models.market.Orderbook;
import ru.tinkoff.invest.openapi.models.market.TradeStatus;
import ru.tinkoff.invest.openapi.models.streaming.StreamingEvent;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.tinkoff.invest.openapi.models.streaming.StreamingRequest.subscribeCandle;
import static ru.tinkoff.invest.openapi.models.streaming.StreamingRequest.subscribeOrderbook;

@Slf4j
@Service
@RequiredArgsConstructor
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CombinedAnalytic {

    private final static int ORDERBOOK_DEPTH = 20;
    private final static BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private final Trader trader;

    @Autowired
    private OpenApi api;
    @Autowired
    private TinkoffProps tinkoffProps;

    private BigDecimal liquidityPer1Min = BigDecimal.ZERO;
    private BigDecimal liquidityPer5Min = BigDecimal.ZERO;
    private BigDecimal price = new BigDecimal(0);

    @PostConstruct
    private void init() {
        api.getStreamingContext().sendRequest(subscribeOrderbook(trader.getConfig().getFigi(), ORDERBOOK_DEPTH));
        api.getStreamingContext().sendRequest(subscribeCandle(trader.getConfig().getFigi(), CandleInterval.ONE_MIN));
        api.getStreamingContext().sendRequest(subscribeCandle(trader.getConfig().getFigi(), CandleInterval.FIVE_MIN));
    }

    @Scheduled(fixedDelay = 5000)
    private void pullData() {
        log.info("Scheduled new pull operation for {}", trader.getConfig());
        api.getMarketContext().getMarketOrderbook(trader.getConfig().getFigi(), 20)
                .thenAccept(opt -> opt.ifPresent(this::orderbookProcess));
    }

    @EventListener(NextCandleEvent.class)
    public void nextCandle(NextCandleEvent event) {
        StreamingEvent.Candle candle = event.getData();
        if (!candle.getFigi().equals(trader.getConfig().getFigi())) {
            return;
        }
        log.info("Catch new candle event {}", candle);
        updateAverageTradingValue(candle);
    }

    private void updateAverageTradingValue(StreamingEvent.Candle candle) {
        switch (candle.getInterval()) {
            case ONE_MIN:
                liquidityPer1Min = candle.getTradingValue()
                        .add(liquidityPer1Min)
                        .divide(new BigDecimal(2), BigDecimal.ROUND_UP);
            case FIVE_MIN:
                liquidityPer5Min = candle.getTradingValue()
                        .add(liquidityPer5Min)
                        .divide(new BigDecimal(2), BigDecimal.ROUND_UP);
        }
    }

    private void orderbookProcess(Orderbook orderbook) {
        if (orderbook.lastPrice == null) {
            log.warn("Orderbook last price is null");
            return;
        }
        if (orderbook.tradeStatus.equals(TradeStatus.NotAvailableForTrading)) {
            log.info("NotAvailableForTrading status for {}", trader.getConfig());
            return;
        }
        BigDecimal asksDeviation = price.subtract(orderbook.asks.isEmpty() ?
                price : orderbook.asks.get(orderbook.asks.size() - 1).price).abs();
        BigDecimal bidsDeviation = price.subtract(orderbook.bids.isEmpty() ?
                price : orderbook.bids.get(orderbook.bids.size() - 1).price).abs();
        BigDecimal maxDeviation = asksDeviation.compareTo(bidsDeviation) > 0 ? asksDeviation : bidsDeviation;

        Map<BigDecimal, BigDecimal> asksMap = orderbook.asks.stream()
                .collect(Collectors.toMap(k -> k.price, v -> v.quantity));
        Map<BigDecimal, BigDecimal> bidsMap = orderbook.bids.stream()
                .collect(Collectors.toMap(k -> k.price, v -> v.quantity));

        BigDecimal startUp = price.add(orderbook.minPriceIncrement);
        BigDecimal startDown = price.subtract(orderbook.minPriceIncrement);
        BigDecimal stop = BigDecimal.ZERO;
        List<BigDecimal> flatQuantity = new ArrayList<>();
        while (stop.compareTo(maxDeviation) <= 0) {
            BigDecimal demand = asksMap.getOrDefault(startDown, BigDecimal.valueOf(0));
            BigDecimal supply = bidsMap.getOrDefault(startUp, BigDecimal.valueOf(0));
            flatQuantity.add(demand.subtract(supply));
            startDown = startDown.subtract(orderbook.minPriceIncrement);
            startUp = startUp.add(orderbook.minPriceIncrement);
            stop = stop.add(orderbook.minPriceIncrement);
        }
        int points = flatQuantity.size();
        List<BigDecimal> rounds = new ArrayList<>();
        for (int i = 0; i < points; i++) {
            BigDecimal round = BigDecimal.ZERO;
            for (int j = i; j < points; j++) {
                round = round.add(flatQuantity.get(j).divide(BigDecimal.valueOf(2), BigDecimal.ROUND_HALF_UP));
            }
            rounds.add(round);
        }

        BigDecimal dsCoefficient = rounds.stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal priceChange = dsCoefficient.multiply(orderbook.minPriceIncrement);

        BigDecimal oneMinChance = HUNDRED.divide(dsCoefficient.abs().divide(
                liquidityPer1Min.divide(HUNDRED, BigDecimal.ROUND_HALF_UP), BigDecimal.ROUND_HALF_UP),
                BigDecimal.ROUND_HALF_UP);

        BigDecimal fiveMinChanse = HUNDRED.divide(dsCoefficient.abs().divide(
                liquidityPer5Min.divide(HUNDRED, BigDecimal.ROUND_HALF_UP), BigDecimal.ROUND_HALF_UP),
                BigDecimal.ROUND_HALF_UP);

        BigDecimal brokerTax = orderbook.lastPrice.add(priceChange.abs())
                .multiply(BigDecimal.valueOf(tinkoffProps.getBrokerTax() / 100));
        BigDecimal countryTax = priceChange
                .multiply(BigDecimal.valueOf(tinkoffProps.getCountryTax() / 100));

        BigDecimal yield = priceChange.subtract(brokerTax.add(countryTax));
        BigDecimal yieldPercents = yield.divide(orderbook.lastPrice.divide(HUNDRED, BigDecimal.ROUND_HALF_UP),
                BigDecimal.ROUND_HALF_UP);
        AnalyticReport report = AnalyticReport.builder()
                .lastPrice(orderbook.lastPrice)
                .priceChange(priceChange)
                .yieldWithTax(yield)
                .yieldWithTaxPercents(yieldPercents)
                .dsCoefficient(dsCoefficient)
                .oneMinChance(oneMinChance)
                .fiveMinChance(fiveMinChanse)
                .oneMinLiquidity(liquidityPer1Min)
                .fiveMinLiquidity(liquidityPer5Min)
                .direction(dsCoefficient.compareTo(BigDecimal.ZERO))
                .build();

        trader.getState().setLastReport(report);
    }
}
