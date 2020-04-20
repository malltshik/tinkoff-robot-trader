package ru.malltshik.trobot.services.candle.events;

import org.springframework.context.ApplicationEvent;
import ru.malltshik.trobot.entities.Candle;

public class NextCandleReceivedEvent extends ApplicationEvent {
    public NextCandleReceivedEvent(Candle candle) {
        super(candle);
    }

    @Override
    public Candle getSource() {
        return (Candle) source;
    }
}
