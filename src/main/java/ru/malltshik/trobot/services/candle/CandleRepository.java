package ru.malltshik.trobot.services.candle;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.malltshik.trobot.entities.Candle;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

@Repository
interface CandleRepository extends CrudRepository<Candle, Long> {
    @Query(value = "select 1 from create_candle_partition(:figi, :dateTime)", nativeQuery = true)
    void createCandlePartition(@Param("figi") String figi, @Param("dateTime") ZonedDateTime dateTime);
}
