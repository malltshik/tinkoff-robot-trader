package ru.malltshik.trobot.trading.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.malltshik.trobot.trading.Broker;
import ru.tinkoff.invest.openapi.OpenApi;
import ru.tinkoff.invest.openapi.models.orders.LimitOrder;
import ru.tinkoff.invest.openapi.models.orders.MarketOrder;
import ru.tinkoff.invest.openapi.models.orders.Order;
import ru.tinkoff.invest.openapi.models.orders.PlacedOrder;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonBroker implements Broker {
    private final OpenApi api;

    @NotNull
    @Override
    public PlacedOrder order(@NotNull String figi, @NotNull LimitOrder order) {
        return api.getOrdersContext().placeLimitOrder(figi, order, null).join();
    }

    @NotNull
    @Override
    public PlacedOrder order(@NotNull String figi, @NotNull MarketOrder order) {
        return api.getOrdersContext().placeMarketOrder(figi, order, null).join();
    }

    @NotNull
    @Override
    public PlacedOrder order(@NotNull String figi, @NotNull LimitOrder order, long timeToComplete) {
        throw new IllegalStateException("Not implemented");
    }

    @NotNull
    @Override
    public PlacedOrder order(@NotNull String figi, @NotNull MarketOrder order, long timeToComplete) {
        throw new IllegalStateException("Not implemented");
    }

    @NotNull
    @Override
    public List<Order> orders() {
        return api.getOrdersContext().getOrders(null).join();
    }

    @Override
    public void cancel(@NotNull String id) {
        api.getOrdersContext().cancelOrder(id, null).join();
    }
}
