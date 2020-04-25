package ru.malltshik.trobot.exceptions;

import javafx.util.Pair;
import org.springframework.http.HttpStatus;

public class InstrumentNotFoundException extends HttpException {
    public InstrumentNotFoundException(String figi) {
        super("instrument.not.found", HttpStatus.NOT_FOUND, new Pair<>("figi", figi));
    }
}
