package ru.malltshik.trobot.events;

import org.springframework.context.ApplicationEvent;
import ru.malltshik.trobot.services.trader.TraderService;

public class TraderServiceRegisterEvent extends ApplicationEvent {
    public TraderServiceRegisterEvent(TraderService traderService) {
        super(traderService);
    }
}
