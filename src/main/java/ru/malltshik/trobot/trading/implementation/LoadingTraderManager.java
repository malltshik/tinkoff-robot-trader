package ru.malltshik.trobot.trading.implementation;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import ru.malltshik.trobot.entities.TraderConfig;
import ru.malltshik.trobot.exceptions.InstrumentNotFoundException;
import ru.malltshik.trobot.repositories.TraderConfigRepository;
import ru.malltshik.trobot.trading.Trader;
import ru.malltshik.trobot.trading.TraderManager;
import ru.tinkoff.invest.openapi.OpenApi;
import ru.tinkoff.invest.openapi.models.market.Instrument;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoadingTraderManager implements TraderManager {

    private final TraderConfigRepository configRepository;
    private final ApplicationContext applicationContext;
    private final OpenApi openApi;

    private AsyncLoadingCache<Long, Trader> cache;
    private Set<Long> keys = new HashSet<>();

    @PostConstruct
    public void initialize() {
        this.cache = Caffeine.newBuilder()
                .removalListener((RemovalListener<Long, Trader>) (key, value, cause) -> {
                    log.warn("Removing trading node {} cause {}", key, cause);
                    if (value != null && value.stop(true)) {
                        keys.remove(key);
                    }
                }).buildAsync(key -> {
                    Optional<TraderConfig> config = configRepository.findById(key);
                    return config.map(c -> createNode(c).join()).orElse(null);
                });
        configRepository.findAll().forEach(this::register);
    }

    @NotNull
    @Override
    public Trader register(@NotNull TraderConfig config) {
        Objects.requireNonNull(config);
        Objects.requireNonNull(config.getId());
        CompletableFuture<Trader> future = createNode(config);
        future.thenAccept(t -> keys.add(config.getId()));
        cache.put(config.getId(), future);
        return future.join();
    }

    @Override
    public void unregister(@NotNull Long key) {
        Objects.requireNonNull(key);
        cache.synchronous().invalidate(key);
        configRepository.deleteById(key);
    }

    @NotNull
    @Override
    public List<Trader> getAll() {
        return new ArrayList<>(cache.getAll(keys).join().values());
    }

    @NotNull
    @Override
    public Optional<Trader> findOne(@NotNull Long key) {
        Objects.requireNonNull(key);
        return Optional.ofNullable(cache.synchronous().get(key));
    }

    @NotNull
    private CompletableFuture<Trader> createNode(@NotNull TraderConfig config) {
        Objects.requireNonNull(config);

        Optional<Instrument> instrument = openApi.getMarketContext()
                .searchMarketInstrumentByFigi(config.getFigi()).join();

        if (instrument.isPresent()) {
            return CompletableFuture.supplyAsync(() ->
                    applicationContext.getBean(Trader.class, instrument.get(), config));
        } else {
            configRepository.deleteById(config.getId());
            throw new InstrumentNotFoundException(config.getFigi());
        }
    }
}
