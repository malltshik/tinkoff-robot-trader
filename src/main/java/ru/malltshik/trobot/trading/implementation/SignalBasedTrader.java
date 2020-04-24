package ru.malltshik.trobot.trading.implementation;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.malltshik.trobot.entities.TraderConfig;
import ru.malltshik.trobot.enums.TraderStatus;
import ru.malltshik.trobot.trading.Analytic;
import ru.malltshik.trobot.trading.Broker;
import ru.malltshik.trobot.trading.Signal;
import ru.malltshik.trobot.trading.Trader;
import ru.tinkoff.invest.openapi.OpenApi;
import ru.tinkoff.invest.openapi.models.streaming.StreamingEvent.InstrumentInfo;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

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
    @Autowired
    private ApplicationEventPublisher publisher;

    // STATE
    private TraderState traderState;

    public SignalBasedTrader(TraderConfig config) {
        this.traderState = TraderState.builder()
                .config(config)
                .status(TraderStatus.DOWN)
                .build();
    }

    @PostConstruct
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void init() {
        traderState.setOnUpdate(publisher::publishEvent);
        this.api.getStreamingContext().sendRequest(subscribeInstrumentInfo(traderState.getConfig().getFigi()));
        analytics(traderState.getConfig(), this::signalConsumer);
    }

    @EventListener(InstrumentInfo.class)
    public void onInstrumentInfoEvent(InstrumentInfo event) {
        if (!event.getFigi().equals(traderState.getConfig().getFigi())) {
            return;
        }
        this.traderState.setInfo(event);
    }

    private void signalConsumer(Signal signal) {
        log.info("Received signal {}", signal);
        // Processing signal and buy or sell
    }

    @NotNull
    @Override
    public TraderConfig getConfig() {
        return traderState.getConfig();
    }

    @NotNull
    @Override
    public Long getKey() {
        return traderState.getConfig().getId();
    }

    @NotNull
    @Override
    public TraderState getState() {
        return this.traderState;
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

    @Lookup
    @SuppressWarnings("UnusedReturnValue")
    private List<Analytic> analytics(TraderConfig config, Consumer<Signal> consumer) {
        return Collections.emptyList();
    }
}
