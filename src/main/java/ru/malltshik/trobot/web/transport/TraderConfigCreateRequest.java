package ru.malltshik.trobot.web.transport;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
public class TraderConfigCreateRequest {
    @NotEmpty(message = "trader.config.create.figi.empty")
    private String figi;
}
