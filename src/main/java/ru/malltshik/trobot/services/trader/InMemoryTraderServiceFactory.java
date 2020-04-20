package ru.malltshik.trobot.services.trader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryTraderServiceFactory implements TraderServiceFactory {

    private final ApplicationContext context;

    private final static ConcurrentHashMap<String, TraderService> STORE = new ConcurrentHashMap<>();

    @NotNull
    @Override
    public Optional<TraderService> findOne(@NotNull String figi) {
        log.info("Find trader with figi {}", figi);
        Objects.requireNonNull(figi);
        return Optional.ofNullable(STORE.get(figi));
    }

    @NotNull
    @Override
    public TraderService register(@NotNull String figi) {
        log.info("New trader registration with figi {}", figi);
        Objects.requireNonNull(figi);
        Optional<TraderService> trader = findOne(figi);
        if (trader.isPresent()) {
            log.info("Trader already exists. Return from registry cache {}", trader.get());
            return trader.get();
        } else {
            TraderService bean = STORE.put(figi, context.getBean(TraderService.class, figi));
            Objects.requireNonNull(bean);
            log.info("New trader has been registered {}", bean);
            return bean;
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
            STORE.remove(figi);
            log.info("Trader with figi {} has been unregistered", figi);
        });
        return traderServiceOptional;
    }
}
