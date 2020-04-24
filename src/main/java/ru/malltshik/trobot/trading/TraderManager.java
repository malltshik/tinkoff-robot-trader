package ru.malltshik.trobot.trading;

import org.jetbrains.annotations.NotNull;
import ru.malltshik.trobot.entities.TraderConfig;

import java.util.List;
import java.util.Optional;

public interface TraderManager {
    @NotNull
    Trader register(@NotNull TraderConfig config);

    void unregister(@NotNull Trader node);

    void unregister(@NotNull TraderConfig config);

    void unregister(@NotNull Long key);

    @NotNull
    List<Trader> getAll();

    @NotNull
    Optional<Trader> findOne(@NotNull Long key);
}
