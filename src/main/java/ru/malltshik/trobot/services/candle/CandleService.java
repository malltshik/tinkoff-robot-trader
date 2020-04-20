package ru.malltshik.trobot.services.candle;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import ru.malltshik.trobot.entities.Candle;
import ru.malltshik.trobot.services.candle.events.NextCandlePersistedEvent;
import ru.tinkoff.invest.openapi.models.market.CandleInterval;

import javax.transaction.Transactional;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CandleService {

    private final CandleRepository candleRepository;
    private final ApplicationEventPublisher eventPublisher;

    @NotNull
    @Transactional
    public Candle save(@NotNull Candle candle) {
        Objects.requireNonNull(candle, "Argument candle cannot be null");

        candleRepository.createCandlePartition(candle.getFigi(), candle.getDateTime());
        Candle save = candleRepository.save(candle);

        eventPublisher.publishEvent(new NextCandlePersistedEvent(save));

        return save;
    }

    @NotNull
    public Optional<Candle> findLast(@NotNull String figi, @NotNull CandleInterval interval) {
        Objects.requireNonNull(figi, "Argument 'figi' cannot be null");
        Objects.requireNonNull(interval, "Argument 'interval' cannot be null");

        return candleRepository.findFirstByFigiAndIntervalOrderByDateTimeDesc(figi, interval);
    }
}
