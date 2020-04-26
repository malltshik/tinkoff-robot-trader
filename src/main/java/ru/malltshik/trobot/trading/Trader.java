package ru.malltshik.trobot.trading;

import org.jetbrains.annotations.NotNull;
import ru.malltshik.trobot.trading.implementation.data.TraderState;

public interface Trader {

    @NotNull
    TraderState getState();

    boolean start();

    boolean stop();

    boolean isRunning();
}
