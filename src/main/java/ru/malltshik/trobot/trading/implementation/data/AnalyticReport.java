package ru.malltshik.trobot.trading.implementation.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticReport {
    private BigDecimal oneMinLiquidity;
    private BigDecimal fiveMinLiquidity;
    private BigDecimal lastPrice;
    private BigDecimal dsCoefficient;
    private BigDecimal oneMinChance;
    private BigDecimal fiveMinChance;
    private BigDecimal priceChange;
    private BigDecimal yieldWithTax;
    private BigDecimal yieldWithTaxPercents;
    private int direction;
}
