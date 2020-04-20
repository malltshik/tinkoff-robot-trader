package ru.malltshik.trobot.services.candle;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.malltshik.trobot.BaseIT;
import ru.malltshik.trobot.entities.Candle;
import ru.tinkoff.invest.openapi.models.market.CandleInterval;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CandleServiceIT extends BaseIT {

    @Autowired
    private CandleService candleService;

    @Test
    @Transactional
    public void save() {
        String figi = "BBG005DXJS36";
        ZonedDateTime now = ZonedDateTime.now();
        BigDecimal numeric = new BigDecimal(100);
        CandleInterval interval = CandleInterval.ONE_MIN;
        Candle candle = createCandle(figi, now, numeric, interval);
        Candle save = candleService.save(candle);
        assertNotNull(save);
        assertNotNull(save.getId());
        assertEquals(save.getFigi(), figi);
        assertEquals(save.getDateTime(), now);
        assertEquals(save.getInterval(), interval);
        assertEquals(save.getOpenPrice(), numeric);
        assertEquals(save.getClosingPrice(), numeric);
        assertEquals(save.getLowestPrice(), numeric);
        assertEquals(save.getHighestPrice(), numeric);
        assertEquals(save.getTradingValue(), numeric);
    }

    @Test
    @Transactional
    public void findLast() {
        String figi = "BBG005DXJS36";
        BigDecimal numeric = new BigDecimal(100);
        CandleInterval interval = CandleInterval.ONE_MIN;
        Candle one = createCandle(figi, ZonedDateTime.now(), numeric, interval);

        Candle first = candleService.save(one);
        assertNotNull(first);
        assertNotNull(first.getId());

        Candle two = createCandle(figi, ZonedDateTime.now(), numeric, interval);
        Candle last = candleService.save(two);
        assertNotNull(last);
        assertNotNull(last.getId());

        assertNotEquals(first.getId(), last.getId());

        Optional<Candle> result = candleService.findLast(figi, interval);
        assertTrue(result.isPresent());
        assertEquals(last, result.get());
    }

    private Candle createCandle(String figi, ZonedDateTime now, BigDecimal numeric, CandleInterval interval) {
        Candle candle = new Candle();
        candle.setFigi(figi);
        candle.setDateTime(now);
        candle.setOpenPrice(numeric);
        candle.setClosingPrice(numeric);
        candle.setHighestPrice(numeric);
        candle.setLowestPrice(numeric);
        candle.setTradingValue(numeric);
        candle.setInterval(interval);
        return candle;
    }
}