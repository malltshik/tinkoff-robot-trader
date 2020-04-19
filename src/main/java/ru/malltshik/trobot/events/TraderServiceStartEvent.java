package ru.malltshik.trobot.events;

import org.springframework.context.ApplicationEvent;
import ru.malltshik.trobot.entities.Candle;
import ru.malltshik.trobot.services.trader.TraderService;

public class TraderServiceStartEvent extends ApplicationEvent {
    public TraderServiceStartEvent(TraderService traderService) {
        super(traderService);
    }
}
