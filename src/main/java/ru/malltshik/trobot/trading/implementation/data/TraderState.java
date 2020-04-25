package ru.malltshik.trobot.trading.implementation.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.malltshik.trobot.entities.TraderConfig;
import ru.malltshik.trobot.enums.TraderStatus;
import ru.tinkoff.invest.openapi.models.market.Instrument;
import ru.tinkoff.invest.openapi.models.streaming.StreamingEvent;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TraderState {
    private Instrument instrument;
    private StreamingEvent.InstrumentInfo info;
    private TraderConfig config;
    private TraderStatus status;
}