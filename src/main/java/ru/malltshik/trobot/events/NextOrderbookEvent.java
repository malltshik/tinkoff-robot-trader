package ru.malltshik.trobot.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import ru.tinkoff.invest.openapi.models.streaming.StreamingEvent;

public class NextOrderbookEvent extends ApplicationEvent {

    @Getter
    private final StreamingEvent.Orderbook data;

    public NextOrderbookEvent(Object source) {
        super(source);
        this.data = (StreamingEvent.Orderbook) source;
    }

}
