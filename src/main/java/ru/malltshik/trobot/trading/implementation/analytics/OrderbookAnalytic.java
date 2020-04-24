package ru.malltshik.trobot.trading.implementation.analytics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.malltshik.trobot.entities.TraderConfig;
import ru.malltshik.trobot.trading.Analytic;
import ru.malltshik.trobot.trading.Signal;
import ru.tinkoff.invest.openapi.OpenApi;
import ru.tinkoff.invest.openapi.models.streaming.StreamingEvent.Orderbook;
import ru.tinkoff.invest.openapi.models.streaming.StreamingRequest;

import javax.annotation.PostConstruct;
import java.util.Objects;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class OrderbookAnalytic implements Analytic {

    private final Consumer<Signal> consumer;
    private final TraderConfig config;

    @Autowired
    private OpenApi api;

    @PostConstruct
    private void init() {
        api.getStreamingContext().sendRequest(StreamingRequest.subscribeOrderbook(config.getFigi(), 10));
    }

    @EventListener(Orderbook.class)
    public void nextOrderbook(Orderbook event) {
        Objects.requireNonNull(event);
        if (!Objects.equals(event.getFigi(), config.getFigi())) {
            return;
        }
        // TODO analize orderbook and send signal
        // consumer.accept(new OrderbookSignal(0));
    }
}
