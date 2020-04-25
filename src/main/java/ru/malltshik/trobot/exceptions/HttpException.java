package ru.malltshik.trobot.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javafx.util.Pair;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(value = {"stackTrace", "cause"})
public class HttpException extends RuntimeException {

    protected String message;
    protected HttpStatus status;
    protected Pair<String, Object>[] params;

    @SafeVarargs
    public HttpException(String message, HttpStatus status, Pair<String, Object>... params) {
        super(message);
        this.status = status;
        this.message = message;
        this.params = params;
    }

    @SafeVarargs
    public HttpException(String message, Pair<String, Object>... params) {
        super(message);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
        this.message = message;
        this.params = params;
    }
}
