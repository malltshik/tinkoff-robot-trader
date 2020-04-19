package ru.malltshik.trobot.services.candle;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.malltshik.trobot.entities.Candle;

import javax.transaction.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JpaCandleService implements CandleService {

    private final CandleRepository candleRepository;

    @Override
    @Transactional
    public Candle save(Candle candle) {
        candleRepository.createCandlePartition(candle.getFigi(), candle.getDateTime());
        return candleRepository.save(candle);
    }

}
