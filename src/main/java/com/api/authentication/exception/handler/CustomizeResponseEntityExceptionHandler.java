package com.api.authentication.exception.handler;

import com.api.authentication.exception.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@RestController
@ControllerAdvice
public class CustomizeResponseEntityExceptionHandler {


    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ExceptionResponse> handleAllExceptions(Exception exception, WebRequest request) {

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

//    @ExceptionHandler(AutheticationException.class)
//    public final ResponseEntity<ExceptionResponse> handleBadResquestExceptions(Exception exception, WebRequest request) {
//        ExceptionResponse exceptionResponse = new ExceptionResponse(new Date(), exception.getMessage(), request.getDescription(false));
//
//        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
//    }

    private String setField(Exception exception) {
        if (exception.getMessage().compareTo("cpfCnpj is mandatory field") == 0 || exception.getMessage().compareTo("cpfCnpj field has a maximum size of 18 characters") == 0) {
            return "cpfCnpj";
        }

        else if (exception.getMessage().compareTo("email is mandatory field") == 0
                || exception.getMessage().compareTo("email field has a maximum size of 50 characters") == 0
                || exception.getMessage().compareTo("email invalid") == 0) {
            return "email";
        }

        else if(exception.getMessage().compareTo("name is mandatory field") == 0 || exception.getMessage().compareTo("name field has a maximum size of 30 characters") == 0) {
            return "name";
        }

        else if(exception.getMessage().compareTo("password is mandatory field") == 0 || exception.getMessage().compareTo("password field has size minimum of 8 and a maximum of 20 characters") == 0) {
            return "password";
        }

        return null;
    }

}
