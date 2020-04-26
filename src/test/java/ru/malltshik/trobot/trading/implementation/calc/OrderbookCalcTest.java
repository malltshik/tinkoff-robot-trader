package ru.malltshik.trobot.trading.implementation.calc;

import org.junit.Test;
import ru.tinkoff.invest.openapi.models.market.Orderbook;
import ru.tinkoff.invest.openapi.models.market.TradeStatus;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static ru.malltshik.trobot.trading.implementation.calc.OrderbookCalc.calcFlatQuantities;
import static ru.malltshik.trobot.trading.implementation.calc.OrderbookCalc.calcMaxDeviation;

public class OrderbookCalcTest {

    @Test
    public void calcMaxDeviationTest() {
        List<Orderbook.Item> asks = Arrays.asList(
                new Orderbook.Item(BigDecimal.valueOf(14.99), BigDecimal.valueOf(1)),
                new Orderbook.Item(BigDecimal.valueOf(14.95), BigDecimal.valueOf(1))
        );
        List<Orderbook.Item> bids = Arrays.asList(
                new Orderbook.Item(BigDecimal.valueOf(15.04), BigDecimal.valueOf(1)),
                new Orderbook.Item(BigDecimal.valueOf(15.06), BigDecimal.valueOf(1))
        );

        Orderbook ob = new Orderbook("", 0, bids, asks, TradeStatus.NormalTrading,
                BigDecimal.valueOf(0.01), null, BigDecimal.valueOf(15.0), null, null, null);

        BigDecimal maxDeviation = calcMaxDeviation(ob);
        assertEquals(BigDecimal.valueOf(0.06), maxDeviation);
    }

    @Test
    public void calcFlatQuantitiesTest() {
        List<Orderbook.Item> asks = Arrays.asList(
                new Orderbook.Item(BigDecimal.valueOf(14.99), BigDecimal.valueOf(1)),
                new Orderbook.Item(BigDecimal.valueOf(14.97), BigDecimal.valueOf(1))
        );
        List<Orderbook.Item> bids = Arrays.asList(
                new Orderbook.Item(BigDecimal.valueOf(15.01), BigDecimal.valueOf(1)),
                new Orderbook.Item(BigDecimal.valueOf(15.04), BigDecimal.valueOf(1))
        );

        Orderbook ob = new Orderbook("", 0, bids, asks, TradeStatus.NormalTrading,
                BigDecimal.valueOf(0.01), null, BigDecimal.valueOf(15.0), null, null, null);

        List<BigDecimal> flatQuantity = calcFlatQuantities(ob);
        assertThat(flatQuantity, hasSize(4));
        assertEquals(BigDecimal.ZERO, flatQuantity.get(0));
        assertEquals(BigDecimal.ZERO, flatQuantity.get(1));
        assertEquals(BigDecimal.ONE, flatQuantity.get(2));
        assertEquals(BigDecimal.valueOf(-1), flatQuantity.get(3));
    }
}