package com.api.authentication.dtos;

import lombok.Data;

@Data
public class CreateUserDto {
    private  String externalId;
    private String cpfCnpj;
    private  String email;
    private  String name;

}
