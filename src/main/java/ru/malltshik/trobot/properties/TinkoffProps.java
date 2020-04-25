package ru.malltshik.trobot.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.tinkoff.invest.openapi.models.Currency;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Core Tinkoff OpenAPI properties for our application.
 * <p>
 * More details here: https://tinkoffcreditsystems.github.io/invest-openapi/
 */
@Data
@ConfigurationProperties(prefix = "tinkoff")
public class TinkoffProps {

    /**
     * Tinkoff invest token. More details how to get it here:
     */
    @NotEmpty(message = "tinkoff.token is required configuration property")
    private String token;

    /**
     * This flag shows as is application running with sandbox token or not
     */
    private boolean sandbox = true;

    /**
     * Broker tax
     */
    private double brokerTax = 0.3;

    /**
     * Country tax
     */
    private double countryTax = 13;

    /**
     * Sandbox init currencies
     */
    private List<CurrencyBalance> currencyBalances = new ArrayList<>();

    /**
     * Thread pool configuration for tinkoff OpenAPI
     */
    private ThreadPoolConfig threadPoolConfig;

    public double getTaxes() {
        return brokerTax + countryTax;
    }

    @Data
    public static class ThreadPoolConfig {
        /**
         * The number of threads to keep in the pool
         */
        @Min(value = 1, message = "Minimum threads in pool must be 1 or more")
        private int min = 1;

        /**
         * The maximum number of threads to allow in the pool
         */
        @Min(value = 1, message = "Maximum threads in pool must be 1 or more")
        private int max = 5;

        /**
         * In milliseconds
         * <p>
         * When the number of threads is greater than the min, this is the maximum time that excess idle
         * threads will wait for new tasks before terminating.
         */
        @Min(value = 1, message = "Keep alive must be more then 0ms")
        private long keepAlive = 180000;
    }

    @Data
    public static class CurrencyBalance {
        private Currency currency;
        private BigDecimal balance;
    }
}
