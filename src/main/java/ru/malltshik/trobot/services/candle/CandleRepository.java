package ru.malltshik.trobot.services.candle;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.malltshik.trobot.entities.Candle;
import ru.tinkoff.invest.openapi.models.market.CandleInterval;

import java.time.ZonedDateTime;
import java.util.Optional;

@Repository
interface CandleRepository extends CrudRepository<Candle, Long> {

    @Query(value = "select 1 from create_candle_partition(:figi, :dateTime)", nativeQuery = true)
    void createCandlePartition(String figi, ZonedDateTime dateTime);

    Optional<Candle> findFirstByFigiAndIntervalOrderByDateTimeDesc(String figi, CandleInterval interval);

    Optional<Candle> findFirstByFigiAndIntervalAndDateTimeBetweenOrderByDateTimeDesc(
            String figi, CandleInterval interval, ZonedDateTime from, ZonedDateTime to);
}
