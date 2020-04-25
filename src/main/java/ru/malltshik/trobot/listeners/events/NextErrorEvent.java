package ru.malltshik.trobot.listeners.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import ru.tinkoff.invest.openapi.models.streaming.StreamingEvent;

public class NextErrorEvent extends ApplicationEvent {

    @Getter
    private final StreamingEvent.Error data;

    public NextErrorEvent(Object source) {
        super(source);
        this.data = (StreamingEvent.Error) source;
    }

}
