package ru.malltshik.trobot.trading.implementation.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.malltshik.trobot.persistance.entities.TraderConfig;
import ru.malltshik.trobot.trading.implementation.data.enums.TraderStatus;
import ru.tinkoff.invest.openapi.models.market.Instrument;
import ru.tinkoff.invest.openapi.models.market.Orderbook;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TraderState {
    private TraderConfig config;
    private TraderStatus status;
    private Instrument instrument;
    private Orderbook lastOrderbook;
    private AnalyticReport lastReport;
}