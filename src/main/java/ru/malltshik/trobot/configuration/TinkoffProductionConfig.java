package ru.malltshik.trobot.configuration;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.ExecutorServiceMetrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import ru.malltshik.trobot.configuration.qualifiers.Production;
import ru.malltshik.trobot.properties.TinkoffProps;
import ru.tinkoff.invest.openapi.OpenApi;
import ru.tinkoff.invest.openapi.models.streaming.StreamingEvent;
import ru.tinkoff.invest.openapi.okhttp.OkHttpOpenApiFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Slf4j
@Configuration
@RequiredArgsConstructor
@Profile("!test")
@ConditionalOnProperty(value = "tinkoff.sandbox", havingValue = "false")
public class TinkoffProductionConfig {

    private final TinkoffProps props;

    @Bean
    @Production
    public ExecutorService productionThreadPoolExecutor(MeterRegistry meterRegistry) {
        String name = "production-thread-pool-executor";
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                props.getThreadPoolConfig().getMin(),
                props.getThreadPoolConfig().getMax(),
                props.getThreadPoolConfig().getKeepAlive(),
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                new CustomizableThreadFactory(name)
        );
        return ExecutorServiceMetrics.monitor(meterRegistry, threadPoolExecutor, name);
    }

    @Bean
    @Production
    public OkHttpOpenApiFactory okHttpOpenApiFactory() {
        Logger logger = Logger.getLogger("Tinkoff-OpenAPI-Logger-Production");
        return new OkHttpOpenApiFactory(props.getToken(), logger);
    }

    @Bean
    @Production
    public OpenApi productionOpenApi(@Production ExecutorService executor,
                                     OkHttpOpenApiFactory factory,
                                     @Autowired(required = false) Subscriber<StreamingEvent> listener) {
        OpenApi api = factory.createOpenApiClient(executor);
        if (listener != null) {
            api.getStreamingContext().getEventPublisher().subscribe(listener);
        }
        return api;
    }

}
