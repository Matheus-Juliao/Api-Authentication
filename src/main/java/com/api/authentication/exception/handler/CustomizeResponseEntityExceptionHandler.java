package com.api.authentication.exception.handler;

import com.api.authentication.configurations.MessageProperty;
import com.api.authentication.exception.ExceptionResponseBadRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@RestController
@ControllerAdvice
@SuppressWarnings("unused")
public class CustomizeResponseEntityExceptionHandler {
    @Autowired
    MessageProperty messageProperty;

    @ExceptionHandler(AuthenticationOperationExceptionBadRequest.class)
    public final @NotNull ResponseEntity<ExceptionResponseBadRequest> handleBadResquestExceptions(Exception exception, WebRequest request) {

        String field = setField(exception);

        ExceptionResponseBadRequest exceptionResponseBadRequest =
                new ExceptionResponseBadRequest(
                        new Date(),
                        exception.getMessage(),
                        field,
                        HttpStatus.BAD_REQUEST.value()
                );


        return new ResponseEntity<>(exceptionResponseBadRequest, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationOperationExceptionNotFound.class)
    public final @NotNull ResponseEntity<ExceptionResponseNotFoundAndUnauthorized> handleNotFoundExceptions(Exception exception, WebRequest request) {

        String field = setField(exception);

        ExceptionResponseNotFoundAndUnauthorized exceptionResponseNotFoundAndUnauthorized =
                new ExceptionResponseNotFoundAndUnauthorized(
                        new Date(),
                        exception.getMessage(),
                        HttpStatus.NOT_FOUND.value()
                );


        return new ResponseEntity<>(exceptionResponseNotFoundAndUnauthorized, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AuthenticationOperationExceptionUnauthorized.class)
    public final @NotNull ResponseEntity<ExceptionResponseNotFoundAndUnauthorized> handleUnauthorizedExceptions(Exception exception, WebRequest request) {

        String field = setField(exception);

        ExceptionResponseNotFoundAndUnauthorized exceptionResponseNotFoundAndUnauthorized =
                new ExceptionResponseNotFoundAndUnauthorized(
                        new Date(),
                        exception.getMessage(),
                        HttpStatus.UNAUTHORIZED.value()
                );


        return new ResponseEntity<>(exceptionResponseNotFoundAndUnauthorized, HttpStatus.UNAUTHORIZED);
    }

    private @Nullable String setField(@NotNull Exception exception)  {

        if (exception.getMessage().compareTo(messageProperty.getProperty("error.cpfCnpj.notNull")) == 0
                || exception.getMessage().compareTo(messageProperty.getProperty("error.cpfCnpj.notBlank")) == 0
                || exception.getMessage().compareTo(messageProperty.getProperty("error.cpfCnpj.size")) == 0) {
            return "cpfCnpj";
        }

        if (exception.getMessage().compareTo(messageProperty.getProperty("error.email.notNull")) == 0
                || exception.getMessage().compareTo(messageProperty.getProperty("error.email.notBlank")) == 0
                || exception.getMessage().compareTo(messageProperty.getProperty("error.email.size")) == 0
                || exception.getMessage().compareTo(messageProperty.getProperty("error.email.invalid")) == 0
                || exception.getMessage().compareTo(messageProperty.getProperty("error.email.notRegistered")) == 0
                || exception.getMessage().compareTo(messageProperty.getProperty("error.account.notRegistered")) == 0) {
            return "email";
        }

        if(exception.getMessage().compareTo(messageProperty.getProperty("error.name.notNull")) == 0
                || exception.getMessage().compareTo(messageProperty.getProperty("error.name.notBlank")) == 0
                || exception.getMessage().compareTo(messageProperty.getProperty("error.name.size")) == 0) {
            return "name";
        }

        if(exception.getMessage().compareTo(messageProperty.getProperty("error.password.notNull")) == 0
                || exception.getMessage().compareTo(messageProperty.getProperty("error.password.notBlank")) == 0
                || exception.getMessage().compareTo(messageProperty.getProperty("error.password.size")) == 0
                || exception.getMessage().compareTo(messageProperty.getProperty("error.password.incorrect")) == 0) {
            return "password";
        }

        return null;
    }

}
