package com.api.authentication.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AuthenticationOperationExceptionNotFound extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public AuthenticationOperationExceptionNotFound(String exception) {
        super(exception);
    }
}
