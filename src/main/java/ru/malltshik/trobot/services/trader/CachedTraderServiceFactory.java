package ru.malltshik.trobot.services.trader;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class CachedTraderServiceFactory implements TraderServiceFactory {

    private final ApplicationContext context;

    private AsyncCache<String, TraderService> cache;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        cache = Caffeine.newBuilder()
                .removalListener((RemovalListener<String, TraderService>) (key, value, cause) -> {
                    log.warn("Removing trader with figi {}. Cause {}", key, cause);
                    if (value != null) {
                        value.stop();
                    }
                }).buildAsync();
    }

    @NotNull
    @Override
    public Optional<TraderService> findOne(@NotNull String figi) {
        log.info("Find trader with figi {}", figi);
        Objects.requireNonNull(figi);
        CompletableFuture<TraderService> future = cache.getIfPresent(figi);
        return future == null ? Optional.empty() : Optional.of(future.join());
    }

    @NotNull
    @Override
    public TraderService register(@NotNull String figi) {
        log.info("New trader registration with figi {}", figi);
        Objects.requireNonNull(figi);
        CompletableFuture<TraderService> future = cache.getIfPresent(figi);
        if (future != null) {
            TraderService trader = future.join();
            log.info("Trader already exists. Return from registry cache {}", trader);
            return trader;
        } else {
            CompletableFuture<TraderService> createFuture = createBean(figi);
            cache.put(figi, createFuture);
            TraderService trader = createFuture.join();
            log.info("New trader has been registered {}", trader);
            return trader;
        }
    }

    @NotNull
    @Override
    public Optional<TraderService> unregister(@NotNull String figi) {
        log.info("Unregister trader with figi {}", figi);
        Objects.requireNonNull(figi);
        Optional<TraderService> traderServiceOptional = findOne(figi);
        traderServiceOptional.ifPresent(traderService -> {
            log.info("Trader with figi {} was found in registry", figi);
            traderService.stop();
            cache.synchronous().invalidate(figi);
            log.info("Trader with figi {} has been unregistered", figi);
        });
        return traderServiceOptional;
    }

    private CompletableFuture<TraderService> createBean(@NotNull String figi) {
        return CompletableFuture.supplyAsync(() -> context.getBean(TraderService.class, figi));
    }
}
