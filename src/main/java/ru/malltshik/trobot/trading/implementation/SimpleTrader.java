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
import ru.malltshik.trobot.trading.implementation.data.Signal;
import ru.malltshik.trobot.trading.implementation.data.TraderState;
import ru.malltshik.trobot.trading.implementation.data.enums.TraderStatus;
import ru.tinkoff.invest.openapi.OpenApi;
import ru.tinkoff.invest.openapi.models.market.Instrument;

import javax.annotation.PostConstruct;

import static ru.tinkoff.invest.openapi.models.streaming.StreamingRequest.subscribeInstrumentInfo;


@Slf4j
@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class SimpleTrader implements Trader {

    // INJECTION
    @Autowired
    private Broker broker;
    @Autowired
    private OpenApi api;
    @Autowired
    private ApplicationContext applicationContext;
    private CombinedAnalytic analytic;

    // STATE
    private TraderState state;

    public SimpleTrader(Instrument instrument, TraderConfig config) {
        this.state = TraderState.builder()
                .instrument(instrument)
                .config(config)
                .status(TraderStatus.DOWN)
                .build();
    }

    @PostConstruct
    private void init() {
        this.api.getStreamingContext().sendRequest(subscribeInstrumentInfo(state.getConfig().getFigi()));
        analytic = applicationContext.getBean(CombinedAnalytic.class, this);
    }

    @Scheduled(fixedDelay = 5000)
    private void pullInstrument() {
        log.info("Scheduled new pull instrument procedure for {}", state.getConfig());
        api.getMarketContext().searchMarketInstrumentByFigi(getConfig().getFigi())
                .thenAccept(opt -> opt.ifPresent(state::setInstrument));
    }

    @Override
    public void onSignal(Signal signal) {
        switch (signal.getType()) {
            case BUY:
            case SELL:
            case HOLD:
        }
    }

    @NotNull
    @Override
    public Long getKey() {
        return state.getConfig().getId();
    }

    @NotNull
    @Override
    public TraderConfig getConfig() {
        return state.getConfig();
    }

    @NotNull
    @Override
    public TraderState getState() {
        return this.state;
    }

    @Override
    public boolean start() {
        if (state.getStatus().equals(TraderStatus.UP)) {
            return true;
        }
        // start operations
        state.setStatus(TraderStatus.UP);
        return true;
    }

    @Override
    public boolean stop() {
        if (state.getStatus().equals(TraderStatus.DOWN)) {
            return true;
        }
        // stop operations
        state.setStatus(TraderStatus.DOWN);
        return true;
    }

    @Override
    public boolean stop(boolean force) {
        if (stop()) {
            return true;
        }
        // force operation
        return true;
    }

    @Override
    public boolean isRunning() {
        return TraderStatus.UP.equals(state.getStatus());
    }
}
