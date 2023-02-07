package com.api.authentication.exception;

import lombok.Getter;

import java.io.Serializable;
import java.util.Date;

@Getter
public class ExceptionResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Date timestamp;
    private String message;
    private String field;
    private Integer code;

    public ExceptionResponse(Date timestamp, String message, String field, Integer code) {
        this.timestamp = timestamp;
        this.message = message;
        this.field = field;
        this.code = code;
    }
}
