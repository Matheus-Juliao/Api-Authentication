package com.api.authentication.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthenticationOperationExceptionUnauthorized extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public AuthenticationOperationExceptionUnauthorized(String exception) {
        super(exception);
    }

}
