package ru.malltshik.trobot.services.candle;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.malltshik.trobot.BaseIntegrationTest;
import ru.malltshik.trobot.entities.Candle;
import ru.tinkoff.invest.openapi.models.market.CandleInterval;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JpaCandleServiceIT extends BaseIntegrationTest {

    @Autowired
    private JpaCandleService candleService;

    @Test
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