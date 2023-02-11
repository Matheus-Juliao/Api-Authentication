package com.api.authentication.exception.handler;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Getter
public class ExceptionResponseNotFoundAndUnauthorized implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Date timestamp;
    private String message;
    private Integer code;

    public ExceptionResponseNotFoundAndUnauthorized(Date timestamp, String message, Integer code) {
        this.timestamp = timestamp;
        this.message = message;
        this.code = code;
    }
}