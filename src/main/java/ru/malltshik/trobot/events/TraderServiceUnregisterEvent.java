package ru.malltshik.trobot.events;

import org.springframework.context.ApplicationEvent;
import ru.malltshik.trobot.services.trader.TraderService;

public class TraderServiceUnregisterEvent extends ApplicationEvent {
    public TraderServiceUnregisterEvent(TraderService traderService) {
        super(traderService);
    }
}
