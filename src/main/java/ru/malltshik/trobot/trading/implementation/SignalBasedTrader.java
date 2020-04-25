package ru.malltshik.trobot.trading.implementation;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.malltshik.trobot.entities.TraderConfig;
import ru.malltshik.trobot.enums.TraderStatus;
import ru.malltshik.trobot.events.NextInstrumentInfoEvent;
import ru.malltshik.trobot.trading.Analytic;
import ru.malltshik.trobot.trading.Broker;
import ru.malltshik.trobot.trading.Trader;
import ru.malltshik.trobot.trading.implementation.data.Signal;
import ru.malltshik.trobot.trading.implementation.data.TraderState;
import ru.tinkoff.invest.openapi.OpenApi;
import ru.tinkoff.invest.openapi.models.market.Instrument;
import ru.tinkoff.invest.openapi.models.streaming.StreamingEvent.InstrumentInfo;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

import static ru.tinkoff.invest.openapi.models.streaming.StreamingRequest.subscribeInstrumentInfo;


@Slf4j
@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class SignalBasedTrader implements Trader {

    // INJECTION
    @Autowired
    private Broker broker;
    @Autowired
    private OpenApi api;

    // STATE
    private TraderState traderState;

    public SignalBasedTrader(Instrument instrument, TraderConfig config) {
        this.traderState = TraderState.builder()
                .instrument(instrument)
                .config(config)
                .status(TraderStatus.DOWN)
                .build();
    }

    @PostConstruct
    private void init() {
        this.api.getStreamingContext().sendRequest(subscribeInstrumentInfo(traderState.getConfig().getFigi()));
        analytics(this);
    }

    @Lookup
    private List<Analytic> analytics(Trader trader) {
        return Collections.emptyList();
    }

    @EventListener(NextInstrumentInfoEvent.class)
    public void onInstrumentInfoEvent(NextInstrumentInfoEvent event) {
        log.info("Receive new instrument info event {}", event);
        InstrumentInfo info = event.getData();
        if (!info.getFigi().equals(traderState.getConfig().getFigi())) {
            return;
        }
        this.traderState.setInfo(info);
    }

    @NotNull
    @Override
    public Long getKey() {
        return traderState.getConfig().getId();
    }

    @NotNull
    @Override
    public TraderConfig getConfig() {
        return traderState.getConfig();
    }

    @NotNull
    @Override
    public TraderState getState() {
        return this.traderState;
    }

    @Override
    public void onSignal(Signal signal) {
        // TODO process signal to sell or buy
        switch (signal.getType()) {
            case BUY:
            case SELL:
            case HOLD:
        }
    }

    @Override
    public boolean start() {
        if (traderState.getStatus().equals(TraderStatus.UP)) {
            return true;
        }
        // start operations
        traderState.setStatus(TraderStatus.UP);
        return true;
    }

    @Override
    public boolean stop() {
        if (traderState.getStatus().equals(TraderStatus.DOWN)) {
            return true;
        }
        // stop operations
        traderState.setStatus(TraderStatus.DOWN);
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
        return TraderStatus.UP.equals(traderState.getStatus());
    }
}
