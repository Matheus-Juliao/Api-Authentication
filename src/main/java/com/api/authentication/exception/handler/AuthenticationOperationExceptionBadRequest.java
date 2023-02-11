package com.api.authentication.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AuthenticationOperationExceptionBadRequest extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public AuthenticationOperationExceptionBadRequest(String exception) {
        super(exception);
    }


}
