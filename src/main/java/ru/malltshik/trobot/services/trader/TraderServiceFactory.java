package ru.malltshik.trobot.services.trader;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface TraderServiceFactory {
    @NotNull
    TraderService register(@NotNull String figi);

    @NotNull
    Optional<TraderService> unregister(@NotNull String figi);

    @NotNull
    Optional<TraderService> findOne(@NotNull String figi);
}
