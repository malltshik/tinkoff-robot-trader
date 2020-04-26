package ru.malltshik.trobot.trading.implementation;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.malltshik.trobot.persistance.entities.TraderConfig;
import ru.malltshik.trobot.trading.Broker;
import ru.malltshik.trobot.trading.Trader;
import ru.malltshik.trobot.trading.implementation.data.AnalyticReport;
import ru.malltshik.trobot.trading.implementation.data.TraderState;
import ru.malltshik.trobot.trading.implementation.data.enums.TraderStatus;
import ru.tinkoff.invest.openapi.OpenApi;
import ru.tinkoff.invest.openapi.models.market.Instrument;

import javax.annotation.PostConstruct;


@Slf4j
@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SimpleTrader implements Trader {

    @Autowired
    private Broker broker;
    @Autowired
    private OpenApi api;
    @Autowired
    private ApplicationContext applicationContext;

    private CombinedAnalytic analytic;
    private Instrument instrument;
    private TraderConfig config;
    private TraderStatus status;
    private AnalyticReport report;

    public SimpleTrader(Instrument instrument, TraderConfig config) {
        this.instrument = instrument;
        this.config = config;
        this.status = TraderStatus.DOWN;
    }

    @PostConstruct
    private void init() {
        analytic = applicationContext.getBean(CombinedAnalytic.class);
    }

    @Scheduled(fixedDelay = 5000)
    private void pullInstrument() {
        if (!isRunning()) return;
        log.info("Scheduled new pull instrument procedure for {}", config);
        api.getMarketContext().searchMarketInstrumentByFigi(config.getFigi())
                .thenAccept(opt -> this.instrument = opt.orElse(null));
    }

    @NotNull
    @Override
    public Long getKey() {
        return config.getId();
    }

    @NotNull
    @Override
    public TraderState getState() {
        return TraderState.builder().instrument(instrument).config(config).report(report).status(status).build();
    }

    @Override
    public boolean start() {
        if (TraderStatus.UP.equals(status)) {
            return true;
        }
        analytic.start(config.getFigi(), this::onReport);
        status = TraderStatus.UP;
        return true;
    }

    @Override
    public boolean stop() {
        if (TraderStatus.DOWN.equals(status)) {
            return true;
        }
        analytic.stop();
        status = TraderStatus.DOWN;
        return true;
    }

    @Override
    public boolean isRunning() {
        return TraderStatus.UP.equals(status);
    }

    private void onReport(AnalyticReport report) {
        this.report = report;
        // TODO buy or sell operations here
    }
}
