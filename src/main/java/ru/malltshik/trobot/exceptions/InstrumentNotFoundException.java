package ru.malltshik.trobot.exceptions;

import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;

public class InstrumentNotFoundException extends HttpException {
    public InstrumentNotFoundException(String figi) {
        super("instrument.not.found", HttpStatus.NOT_FOUND, Pair.of("figi", figi));
    }
}
