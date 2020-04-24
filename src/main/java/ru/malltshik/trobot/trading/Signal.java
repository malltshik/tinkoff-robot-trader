package ru.malltshik.trobot.trading;

import ru.malltshik.trobot.enums.SignalType;

public interface Signal {
    long getWeight();

    SignalType getType();

    Object getPayload();
}
