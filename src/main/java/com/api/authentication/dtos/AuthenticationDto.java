package com.api.authentication.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.UniqueElements;

//Getters e Setters e outros métodos com o lombok
@Data
public class AuthenticationDto {

    @NotBlank(message = "cpfCnpj is mandatory field")
    @NotNull(message = "cpfCnpj is mandatory field")
    @Size(max = 18, message = "cpfCnpj field has a maximum size of 18 characters")
    private String cpfCnpj;

    @NotBlank(message = "email is mandatory field")
    @NotNull(message = "email is mandatory field")
    @Size(max = 50, message = "email field has a maximum size of 50 characters")
    @Email(message = "email invalid")
    private String email;

    @NotBlank(message = "name is mandatory field")
    @NotNull(message = "name is mandatory field")
    @Size(max = 30, message = "Name field has a maximum size of 30 characters")
    private String name;

    @NotBlank(message = "password is mandatory field")
    @NotNull(message = "password is mandatory field")
    @Size(min = 8, max = 20, message = "password field has size minimum of 8 and a maximum of 20 characters")
    private String password;
}
