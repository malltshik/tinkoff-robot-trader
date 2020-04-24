package ru.malltshik.trobot.trading;

import org.jetbrains.annotations.NotNull;
import ru.malltshik.trobot.entities.TraderConfig;
import ru.malltshik.trobot.trading.implementation.TraderState;

public interface Trader {

    @NotNull
    TraderConfig getConfig();

    @NotNull
    Long getKey();

    @NotNull
    TraderState getState();

    boolean start();

    boolean stop();

    boolean stop(boolean force);

    boolean isRunning();
}
