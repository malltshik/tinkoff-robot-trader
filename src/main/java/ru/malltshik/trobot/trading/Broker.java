package ru.malltshik.trobot.trading;

import org.jetbrains.annotations.NotNull;
import ru.tinkoff.invest.openapi.models.orders.LimitOrder;
import ru.tinkoff.invest.openapi.models.orders.MarketOrder;
import ru.tinkoff.invest.openapi.models.orders.Order;
import ru.tinkoff.invest.openapi.models.orders.PlacedOrder;

import java.util.List;


public interface Broker {

    @NotNull
    PlacedOrder order(@NotNull String figi, @NotNull LimitOrder order);

    @NotNull
    PlacedOrder order(@NotNull String figi, @NotNull MarketOrder order);

    @NotNull
    PlacedOrder order(@NotNull String figi, @NotNull LimitOrder order, long timeToComplete);

    @NotNull
    PlacedOrder order(@NotNull String figi, @NotNull MarketOrder order, long timeToComplete);

    @NotNull
    List<Order> orders();

    void cancel(@NotNull String id);

}
