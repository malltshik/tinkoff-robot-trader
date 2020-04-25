package ru.malltshik.trobot.listeners.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import ru.tinkoff.invest.openapi.models.streaming.StreamingEvent;

public class NextCandleEvent extends ApplicationEvent {

    @Getter
    private final StreamingEvent.Candle data;

    public NextCandleEvent(Object source) {
        super(source);
        this.data = (StreamingEvent.Candle) source;
    }

}
