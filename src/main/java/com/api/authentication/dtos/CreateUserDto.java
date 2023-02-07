package com.api.authentication.dtos;

import lombok.Data;

@Data
public class CreateUserDto {
    private  String externalId;
    private String cpfCnpj;
    private  String email;
    private  String name;

    public CreateUserDto () {}

    public CreateUserDto(String externalId, String cpfCnpj, String email, String name) {
        this.externalId = externalId;
        this.cpfCnpj = cpfCnpj;
        this.email = email;
        this.name = name;
    }
}
