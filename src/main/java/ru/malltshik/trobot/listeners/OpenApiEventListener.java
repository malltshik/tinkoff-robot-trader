package ru.malltshik.trobot.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.malltshik.trobot.listeners.events.NextCandleEvent;
import ru.malltshik.trobot.listeners.events.NextErrorEvent;
import ru.malltshik.trobot.listeners.events.NextInstrumentInfoEvent;
import ru.malltshik.trobot.listeners.events.NextOrderbookEvent;
import ru.tinkoff.invest.openapi.models.streaming.StreamingEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenApiEventListener implements Subscriber<StreamingEvent> {

    private final ApplicationEventPublisher publisher;

    @Override
    public void onSubscribe(Subscription s) {
        log.info("Successful subscribe on {}", s);
    }

    @Override
    public void onNext(StreamingEvent event) {
        log.info("Receive new event {}", event);
        if (event instanceof StreamingEvent.Candle) {
            publisher.publishEvent(new NextCandleEvent(event));
        }
        if (event instanceof StreamingEvent.Orderbook) {
            publisher.publishEvent(new NextOrderbookEvent(event));
        }
        if (event instanceof StreamingEvent.InstrumentInfo) {
            publisher.publishEvent(new NextInstrumentInfoEvent(event));
        }
        if (event instanceof StreamingEvent.Error) {
            publisher.publishEvent(new NextErrorEvent(event));
        }
    }

    @Override
    public void onError(Throwable t) {
        log.error("Error in event listener", t);
    }

    @Override
    public void onComplete() {
        log.info("On complete method invoked");
    }
}
