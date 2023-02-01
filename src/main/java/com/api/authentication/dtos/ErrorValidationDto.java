package com.api.authentication.dtos;

import lombok.Data;

import java.util.Date;

@Data
public class ErrorValidationDto {
    private Date timestamp;
    private String message;
    private String field;
    private Integer code;

    public ErrorValidationDto(Date timestamp, Integer code) {
        this.timestamp = timestamp;
        this.code = code;
    }
}
