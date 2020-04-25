package ru.malltshik.trobot.trading.implementation.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.malltshik.trobot.trading.implementation.data.enums.SignalType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Signal {

    private SignalType type;

}
