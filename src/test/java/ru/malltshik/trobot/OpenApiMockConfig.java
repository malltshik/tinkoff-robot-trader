package ru.malltshik.trobot;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.tinkoff.invest.openapi.MarketContext;
import ru.tinkoff.invest.openapi.OpenApi;
import ru.tinkoff.invest.openapi.OperationsContext;
import ru.tinkoff.invest.openapi.OrdersContext;
import ru.tinkoff.invest.openapi.PortfolioContext;
import ru.tinkoff.invest.openapi.StreamingContext;
import ru.tinkoff.invest.openapi.UserContext;
import ru.tinkoff.invest.openapi.models.Currency;
import ru.tinkoff.invest.openapi.models.market.Instrument;
import ru.tinkoff.invest.openapi.models.market.InstrumentType;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@TestConfiguration
public class OpenApiMockConfig {

    @Bean
    public OpenApi openApi(MarketContext marketContext,
                           OrdersContext ordersContext,
                           StreamingContext streamingContext,
                           OperationsContext operationsContext,
                           PortfolioContext portfolioContext,
                           UserContext userContext) {
        OpenApi api = Mockito.mock(OpenApi.class);
        when(api.getMarketContext()).thenReturn(marketContext);
        when(api.getOrdersContext()).thenReturn(ordersContext);
        when(api.getStreamingContext()).thenReturn(streamingContext);
        when(api.getOperationsContext()).thenReturn(operationsContext);
        when(api.getPortfolioContext()).thenReturn(portfolioContext);
        when(api.getUserContext()).thenReturn(userContext);
        return api;
    }

    @Bean
    public MarketContext marketContext() {
        MarketContext marketContext = Mockito.mock(MarketContext.class);

        when(marketContext.searchMarketInstrumentByFigi(anyString())).then(invocation -> {
            String figi = invocation.getArgument(0, String.class);
            if ("200".equals(figi)) {
                Instrument i = new Instrument(figi, figi, null, null, 1, Currency.USD, figi, InstrumentType.Stock);
                return CompletableFuture.completedFuture(Optional.of(i));
            } else {
                return CompletableFuture.completedFuture(Optional.empty());
            }
        });

        return marketContext;
    }

    @Bean
    public OrdersContext ordersContext() {
        OrdersContext ordersContext = Mockito.mock(OrdersContext.class);
        // mock methods
        return ordersContext;
    }

    @Bean
    public StreamingContext streamingContext() {
        StreamingContext streamingContext = Mockito.mock(StreamingContext.class);
        // mock methods
        return streamingContext;
    }

    @Bean
    public OperationsContext operationsContext() {
        OperationsContext operationsContext = Mockito.mock(OperationsContext.class);
        // mock methods
        return operationsContext;
    }

    @Bean
    public PortfolioContext portfolioContext() {
        PortfolioContext portfolioContext = Mockito.mock(PortfolioContext.class);
        // mock methods
        return portfolioContext;
    }

    @Bean
    public UserContext userContext() {
        UserContext userContext = Mockito.mock(UserContext.class);
        // mock methods
        return userContext;
    }

}
