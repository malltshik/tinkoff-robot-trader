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
import ru.tinkoff.invest.openapi.models.market.CandleInterval;
import ru.tinkoff.invest.openapi.models.streaming.StreamingEvent.Candle;

import javax.annotation.PostConstruct;
import java.util.Objects;
import java.util.function.Consumer;

import static ru.tinkoff.invest.openapi.models.streaming.StreamingRequest.subscribeCandle;

@Slf4j
@Service
@RequiredArgsConstructor
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TrendAnalytic implements Analytic {

    private final Consumer<Signal> consumer;
    private final TraderConfig config;

    @Autowired
    private OpenApi api;

    @PostConstruct
    private void init() {
        api.getStreamingContext().sendRequest(subscribeCandle(config.getFigi(), CandleInterval.ONE_MIN));
    }

    @EventListener(Candle.class)
    public void nextOrderbook(Candle event) {
        Objects.requireNonNull(event);
        if (!Objects.equals(event.getFigi(), config.getFigi())) {
            return;
        }
        // TODO analize orderbook and send signal
        // consumer.accept(new OrderbookSignal(0));
    }
}
