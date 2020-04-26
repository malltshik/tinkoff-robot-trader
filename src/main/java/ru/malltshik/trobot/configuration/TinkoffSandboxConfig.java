package ru.malltshik.trobot.configuration;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.malltshik.trobot.configuration.qualifiers.Sandbox;
import ru.malltshik.trobot.properties.TinkoffProps;
import ru.tinkoff.invest.openapi.OpenApi;
import ru.tinkoff.invest.openapi.SandboxOpenApi;
import ru.tinkoff.invest.openapi.models.sandbox.CurrencyBalance;
import ru.tinkoff.invest.openapi.models.streaming.StreamingEvent;
import ru.tinkoff.invest.openapi.okhttp.OkHttpOpenApiFactory;

import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

import static io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics.monitor;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static java.util.logging.Logger.getLogger;

@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(value = "tinkoff.sandbox", havingValue = "true", matchIfMissing = true)
public class TinkoffSandboxConfig {

    private final TinkoffProps props;

    @Bean
    @Sandbox
    public OkHttpOpenApiFactory okHttpOpenApiFactory() {
        Logger logger = getLogger("Tinkoff-OpenAPI-Logger-Sandbox");
        return new OkHttpOpenApiFactory(props.getToken(), logger);
    }

    @Bean
    @Sandbox
    public ExecutorService sandboxThreadPoolExecutor(MeterRegistry meterRegistry) {
        return monitor(meterRegistry, newSingleThreadExecutor(), "sandbox-thread-pool-executor");
    }

    @Bean
    @Sandbox
    public OpenApi sandboxOpenApi(@Sandbox ExecutorService executor,
                                  OkHttpOpenApiFactory factory,
                                  @Autowired(required = false) Subscriber<StreamingEvent> listener) {
        SandboxOpenApi api = factory.createSandboxOpenApiClient(executor);
        api.getSandboxContext().performRegistration(null).join();
        api.getSandboxContext().clearAll(null).join();

        props.getCurrencyBalances().stream()
                .map(c -> new CurrencyBalance(c.getCurrency(), c.getBalance()))
                .forEach(c -> api.getSandboxContext().setCurrencyBalance(c, null).join());

        if (listener != null) {
            api.getStreamingContext().getEventPublisher().subscribe(listener);
        }

        return api;
    }

}
