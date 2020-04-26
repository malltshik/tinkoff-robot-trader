package ru.malltshik.trobot.trading.implementation.calc;

import org.jetbrains.annotations.NotNull;
import ru.tinkoff.invest.openapi.models.market.Orderbook;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class OrderbookCalc {

    /**
     * This method calculate max deviation of price in orderbook from last price.
     * <p>
     * Example:
     * price: 15$
     * asks: {[14.99$, 2],[14.95, 3]}
     * bids: {[15.04$, 6],[15.06, 4]}
     * max deviation: 15$ - 15.06$ = 0.06$
     *
     * @param ob tinkoff api object with price, asks and bids
     * @return max deviation of price in the ob from last price
     */
    @NotNull
    public static BigDecimal calcMaxDeviation(@NotNull Orderbook ob) {
        Objects.requireNonNull(ob, "Orderbook is required");
        Objects.requireNonNull(ob.lastPrice, "Price is required");
        Objects.requireNonNull(ob.asks, "Asks is required");
        Objects.requireNonNull(ob.bids, "Bids is required");

        BigDecimal asksDeviation = ob.lastPrice.subtract(
                ob.asks.stream().map(i -> i.price).min(BigDecimal::compareTo).orElse(ob.lastPrice)).abs();

        BigDecimal bidsDeviation = ob.lastPrice.subtract(
                ob.bids.stream().map(i -> i.price).max(BigDecimal::compareTo).orElse(ob.lastPrice)).abs();

        return asksDeviation.compareTo(bidsDeviation) > 0 ? asksDeviation : bidsDeviation;
    }

    /**
     * List of quantities pairwise combined with respect to price plus each min deviation to both sides
     * <p>
     * Example:
     * asks: {[14.99$, 2],[14.98, 3]}
     * bids: {[15.01$, 6],[15.02, 4]}
     * flat: [(2 - 6), (3 - 4)] = [-4, -1]
     *
     * @param ob tinkoff api object with price, asks and bids
     * @return list of quantities pairwise combined with respect to price
     */
    @NotNull
    public static List<BigDecimal> calcFlatQuantities(@NotNull Orderbook ob) {
        Objects.requireNonNull(ob, "Orderbook is required");
        Objects.requireNonNull(ob.lastPrice, "Price is required");
        Objects.requireNonNull(ob.minPriceIncrement, "Min price increment is required");

        Map<BigDecimal, BigDecimal> asksMap = ob.asks.stream()
                .collect(Collectors.toMap(k -> k.price, v -> v.quantity));
        Map<BigDecimal, BigDecimal> bidsMap = ob.bids.stream()
                .collect(Collectors.toMap(k -> k.price, v -> v.quantity));

        BigDecimal maxDeviation = calcMaxDeviation(ob);

        BigDecimal startUp = ob.lastPrice.add(ob.minPriceIncrement);
        BigDecimal startDown = ob.lastPrice.subtract(ob.minPriceIncrement);
        BigDecimal stop = BigDecimal.ZERO;

        List<BigDecimal> result = new ArrayList<>();

        while (stop.compareTo(maxDeviation) < 0) {
            BigDecimal demand = asksMap.getOrDefault(startDown, BigDecimal.valueOf(0));
            BigDecimal supply = bidsMap.getOrDefault(startUp, BigDecimal.valueOf(0));
            result.add(demand.subtract(supply));
            startDown = startDown.subtract(ob.minPriceIncrement);
            startUp = startUp.add(ob.minPriceIncrement);
            stop = stop.add(ob.minPriceIncrement);
        }
        return result;
    }

}
