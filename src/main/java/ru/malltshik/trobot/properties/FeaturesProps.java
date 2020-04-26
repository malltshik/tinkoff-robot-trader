package ru.malltshik.trobot.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "features")
public class FeaturesProps {

    /**
     * Enable/disable {@link ru.malltshik.trobot.configuration.TinkoffEventListenerConfig}
     */
    private boolean tinkoffListener = true;
}
