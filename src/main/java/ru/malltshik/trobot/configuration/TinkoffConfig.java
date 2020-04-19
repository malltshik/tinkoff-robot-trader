package ru.malltshik.trobot.configuration;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import ru.malltshik.trobot.properties.TinkoffProps;
import ru.tinkoff.invest.openapi.OpenApi;
import ru.tinkoff.invest.openapi.SandboxOpenApi;
import ru.tinkoff.invest.openapi.models.sandbox.CurrencyBalance;
import ru.tinkoff.invest.openapi.models.sandbox.PositionBalance;
import ru.tinkoff.invest.openapi.models.streaming.StreamingEvent;
import ru.tinkoff.invest.openapi.okhttp.OkHttpOpenApiFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static java.lang.String.format;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TinkoffConfig {

    private final List<Subscriber<StreamingEvent>> listeners;

    @Bean
    public Logger logger(TinkoffProps props) {
        return Logger.getLogger(format("Tinkoff-OpenAPI-Logger-%s", props.isSandbox() ? "sandbox" : "production"));
    }

    @Bean
    @ConditionalOnProperty(value = "tinkoff.sandbox", havingValue = "true", matchIfMissing = true)
    public ExecutorService sandboxThreadPoolExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    @Bean
    @ConditionalOnProperty(value = "tinkoff.sandbox", havingValue = "false")
    public ExecutorService productionThreadPoolExecutor(MeterRegistry meterRegistry, TinkoffProps props) {
        String name = "production-thread-pool-executor";
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                props.getThreadPoolConfig().getMin(),
                props.getThreadPoolConfig().getMax(),
                props.getThreadPoolConfig().getKeepAlive(),
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                new CustomizableThreadFactory(name)
        );
        return ExecutorServiceMetrics.monitor(meterRegistry, threadPoolExecutor, "production-thread-pool-executor");
    }

    @Bean
    public OkHttpOpenApiFactory okHttpOpenApiFactory(TinkoffProps props, Logger logger) {
        return new OkHttpOpenApiFactory(props.getToken(), logger);
    }

    @Bean
    @Profile("!test")
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public OpenApi openApi(TinkoffProps props, ExecutorService executor, OkHttpOpenApiFactory factory) {
        OpenApi api;
        if (props.isSandbox()) {
            api = factory.createSandboxOpenApiClient(executor);
            configureSandboxApi(props, (SandboxOpenApi) api);
        } else {
            api = factory.createOpenApiClient(executor);
        }
        listeners.forEach(l -> api.getStreamingContext().getEventPublisher().subscribe(l));
        return api;
    }

    private void configureSandboxApi(TinkoffProps props, SandboxOpenApi api) {
        api.getSandboxContext().performRegistration(null).join();
        api.getSandboxContext().clearAll(null);
        props.getCurrencyBalances().stream()
                .map(c -> new CurrencyBalance(c.getCurrency(), c.getBalance()))
                .forEach(c -> api.getSandboxContext().setCurrencyBalance(c, null));
        props.getPositionBalance().stream()
                .map(p -> new PositionBalance(p.getFigi(), p.getLots()))
                .forEach(p -> api.getSandboxContext().setPositionBalance(p, null));
    }

}
