package ru.malltshik.trobot.events;

import org.springframework.context.ApplicationEvent;
import ru.malltshik.trobot.entities.Candle;

public class NextCandleSavedEvent extends ApplicationEvent {
    public NextCandleSavedEvent(Candle candle) {
        super(candle);
    }
}
