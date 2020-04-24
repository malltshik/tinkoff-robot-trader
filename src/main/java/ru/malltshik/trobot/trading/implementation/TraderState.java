package ru.malltshik.trobot.trading.implementation;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.malltshik.trobot.entities.TraderConfig;
import ru.malltshik.trobot.enums.TraderStatus;
import ru.tinkoff.invest.openapi.models.streaming.StreamingEvent;

import java.util.Observable;
import java.util.Observer;
import java.util.function.Consumer;

import static java.util.Optional.ofNullable;

@Slf4j
@Data
@Builder
public class TraderState implements Observer {
    private Consumer<TraderState> onUpdate;
    private TraderStatus status;
    private TraderConfig config;
    private StreamingEvent.InstrumentInfo info;

    @Override
    public void update(Observable o, Object arg) {
        ofNullable(onUpdate).ifPresent(c -> c.accept(this));
        log.info("State updated {}", this);
    }
}