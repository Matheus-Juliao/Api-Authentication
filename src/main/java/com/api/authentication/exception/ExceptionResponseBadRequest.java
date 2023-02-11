package com.api.authentication.exception;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Getter
public class ExceptionResponseBadRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Date timestamp;
    private final String message;
    private final String field;
    private final Integer code;

    public ExceptionResponseBadRequest(Date timestamp, String message, String field, Integer code) {
        this.timestamp = timestamp;
        this.message = message;
        this.field = field;
        this.code = code;
    }
}
