package ru.malltshik.trobot.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.malltshik.trobot.exceptions.HttpException;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerConfig {

    @ExceptionHandler(HttpException.class)
    public ResponseEntity<?> handle(HttpException e) {
        log.info("Handle http exception {} with params {}", e.getClass(), e.getParams());
        return ResponseEntity.status(e.getStatus()).body(e);
    }

}
