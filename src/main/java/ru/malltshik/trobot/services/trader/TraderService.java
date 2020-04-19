package ru.malltshik.trobot.services.trader;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.malltshik.trobot.events.TraderServiceStartEvent;
import ru.malltshik.trobot.events.TraderServiceStopEvent;

import javax.annotation.PostConstruct;

@Slf4j
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class TraderService {

    // CONSTRUCTOR FIELDS
    @ToString.Include
    private final String figi;

    // INJECTED BEENS
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    // STATEFUL FIELDS
    @Getter
    @ToString.Include
    private Status status = Status.DOWN;

    @PostConstruct
    private void init() {
        // INIT TRADER
    }

    public void start() {
        this.status = Status.UP;
        eventPublisher.publishEvent(new TraderServiceStartEvent(this));
    }

    public void stop() {
        this.status = Status.DOWN;
        eventPublisher.publishEvent(new TraderServiceStopEvent(this));
    }

    public enum Status {
        UP, DOWN
    }
}
