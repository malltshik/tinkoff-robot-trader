package ru.malltshik.trobot.trading.implementation.analytics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.malltshik.trobot.events.NextOrderbookEvent;
import ru.malltshik.trobot.trading.Analytic;
import ru.malltshik.trobot.trading.Trader;
import ru.tinkoff.invest.openapi.OpenApi;
import ru.tinkoff.invest.openapi.models.streaming.StreamingEvent.Orderbook;

import javax.annotation.PostConstruct;
import java.util.Objects;

import static ru.tinkoff.invest.openapi.models.streaming.StreamingRequest.subscribeOrderbook;

@Slf4j
@Service
@RequiredArgsConstructor
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class OrderbookAnalytic implements Analytic {

    private final Trader trader;

    @Autowired
    private OpenApi api;

    @PostConstruct
    private void init() {
        api.getStreamingContext().sendRequest(subscribeOrderbook(trader.getConfig().getFigi(), 20));
    }

    @EventListener(NextOrderbookEvent.class)
    public void nextOrderbook(NextOrderbookEvent event) {
        log.info("Receive new orderbook event {}", event);
        Orderbook orderbook = event.getData();
        if (!Objects.equals(orderbook.getFigi(), trader.getConfig().getFigi())) {
            return;
        }
        // TODO process
        // trader.process(Intention.SELL);
    }
}
