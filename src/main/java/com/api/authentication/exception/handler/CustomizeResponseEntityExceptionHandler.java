package com.api.authentication.exception.handler;

import com.api.authentication.configurations.MessageProperty;
import com.api.authentication.exception.ExceptionResponse;
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

    @ExceptionHandler(Exception.class)
    public final @NotNull ResponseEntity<ExceptionResponse> handleBadResquestExceptions(Exception exception, WebRequest request) {

        String field = setField(exception);

        ExceptionResponse exceptionResponse =
                new ExceptionResponse(
                        new Date(),
                        exception.getMessage(),
                        field,
                        HttpStatus.BAD_REQUEST.value()
                );


        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
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
