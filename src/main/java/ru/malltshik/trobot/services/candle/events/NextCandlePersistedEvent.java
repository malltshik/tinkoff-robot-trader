package ru.malltshik.trobot.services.candle.events;

import org.springframework.context.ApplicationEvent;
import ru.malltshik.trobot.entities.Candle;

public class NextCandlePersistedEvent extends ApplicationEvent {
    public NextCandlePersistedEvent(Candle candle) {
        super(candle);
    }

    @Override
    public Candle getSource() {
        return (Candle) source;
    }
}
