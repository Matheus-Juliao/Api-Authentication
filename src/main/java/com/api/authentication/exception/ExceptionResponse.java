package com.api.authentication.exception;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Getter
public record ExceptionResponse(Date timestamp, String message, String field, Integer code) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

}
