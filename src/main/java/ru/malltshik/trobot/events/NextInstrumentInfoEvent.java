package ru.malltshik.trobot.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import ru.tinkoff.invest.openapi.models.streaming.StreamingEvent;

public class NextInstrumentInfoEvent extends ApplicationEvent {

    @Getter
    private final StreamingEvent.InstrumentInfo data;

    public NextInstrumentInfoEvent(Object source) {
        super(source);
        this.data = (StreamingEvent.InstrumentInfo) source;
    }
    
}
