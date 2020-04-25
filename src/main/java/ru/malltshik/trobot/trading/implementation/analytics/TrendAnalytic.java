package ru.malltshik.trobot.trading.implementation.analytics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.malltshik.trobot.events.NextCandleEvent;
import ru.malltshik.trobot.trading.Analytic;
import ru.malltshik.trobot.trading.Trader;
import ru.tinkoff.invest.openapi.OpenApi;
import ru.tinkoff.invest.openapi.models.market.CandleInterval;
import ru.tinkoff.invest.openapi.models.streaming.StreamingEvent.Candle;

import javax.annotation.PostConstruct;
import java.util.Objects;

import static ru.tinkoff.invest.openapi.models.streaming.StreamingRequest.subscribeCandle;

@Slf4j
@Service
@RequiredArgsConstructor
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TrendAnalytic implements Analytic {

    private final Trader trader;

    @Autowired
    private OpenApi api;

    @PostConstruct
    private void init() {
        api.getStreamingContext().sendRequest(subscribeCandle(trader.getConfig().getFigi(), CandleInterval.ONE_MIN));
    }

    @EventListener(NextCandleEvent.class)
    public void nextOrderbook(NextCandleEvent event) {
        log.info("Receive new candle event {}", event);
        Candle candle = event.getData();
        if (!Objects.equals(candle.getFigi(), trader.getConfig().getFigi())) {
            return;
        }
        // TODO process
        // trader.process(Intention.SELL);
    }
}
