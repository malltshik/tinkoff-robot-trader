package ru.malltshik.trobot.events;

import org.springframework.context.ApplicationEvent;
import ru.malltshik.trobot.services.trader.TraderService;

public class TraderServiceStopEvent extends ApplicationEvent {
    public TraderServiceStopEvent(TraderService traderService) {
        super(traderService);
    }
}
