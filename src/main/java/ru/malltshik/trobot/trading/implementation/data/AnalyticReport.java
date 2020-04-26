package ru.malltshik.trobot.trading.implementation.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticReport {

    private BigDecimal lastPrice;
    private ZonedDateTime created;
    private Forecast bestForecast;
    private List<Forecast> forecasts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Forecast {
        /**
         * Start time of market movement for this forecast
         */
        private ZonedDateTime from;
        /**
         * End time of market movement for this forecast
         */
        private ZonedDateTime to;
        /**
         * Amount of movement
         */
        private int points;
        /**
         * Direction of movement down = -1, up = 1, flat = 0
         */
        private int direction;
        /**
         * Trading value of operation for this forecast
         */
        private BigDecimal operationValue;
        /**
         * Revenue for this forecast
         */
        private BigDecimal revenue;
        /**
         * Sum of taxes (broker & country) for this forecast
         */
        private BigDecimal taxes;
        /**
         * Profit in currency for this forecast
         */
        private BigDecimal profit;
        /**
         * Profit in percents of last price
         */
        private BigDecimal profitPercents;
    }
}

