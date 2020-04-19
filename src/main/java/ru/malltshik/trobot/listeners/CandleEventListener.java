package ru.malltshik.trobot.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import ru.malltshik.trobot.events.NextCandleSavedEvent;
import ru.malltshik.trobot.entities.Candle;
import ru.malltshik.trobot.services.CandleService;
import ru.tinkoff.invest.openapi.models.streaming.StreamingEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class CandleEventListener implements Subscriber<StreamingEvent.Candle> {

    private final ModelMapper mapper;
    private final CandleService candleService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void onSubscribe(Subscription subscription) {
        log.info("Subscribed on {}", subscription);
    }

    @Override
    public void onNext(StreamingEvent.Candle candle) {
        Candle entity = mapper.map(candle, Candle.class);
        Candle saved = candleService.save(entity);
        eventPublisher.publishEvent(new NextCandleSavedEvent(saved));
    }

    @Override
    public void onError(Throwable t) {
        log.error("Error in event listener", t);
    }

    @Override
    public void onComplete() {
        log.info("Subscription complete");
    }
}
