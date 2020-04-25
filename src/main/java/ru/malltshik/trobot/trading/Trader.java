package ru.malltshik.trobot.trading;

import org.jetbrains.annotations.NotNull;
import ru.malltshik.trobot.entities.TraderConfig;
import ru.malltshik.trobot.enums.SignalType;
import ru.malltshik.trobot.trading.implementation.data.Signal;
import ru.malltshik.trobot.trading.implementation.data.TraderState;

public interface Trader {

    @NotNull
    Long getKey();

    @NotNull
    TraderConfig getConfig();

    @NotNull
    TraderState getState();

    void onSignal(Signal signal);

    boolean start();

    boolean stop();

    boolean stop(boolean force);

    boolean isRunning();
}
