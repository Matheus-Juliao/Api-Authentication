package com.api.authentication.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

//Getters e Setters e outros métodos com o lombok
@Data
public class AuthenticationDto {

    @NotBlank(message = "{error.cpfCnpj.notBlank}")
    @NotNull(message = "{error.cpfCnpj.notNull}")
    @Size(max = 18, message = "{error.cpfCnpj.Size}")
    private String cpfCnpj;

    @NotBlank(message = "{error.email.notBlank}")
    @NotNull(message = "{error.email.notNull}")
    @Size(max = 50, message = "{error.email.size}")
    @Email(message = "{error.email.invalid}")
    private String email;

    @NotBlank(message = "{error.name.notBlank}")
    @NotNull(message = "{error.name.notNull}")
    @Size(max = 30, message = "{error.name.size}")
    private String name;

    @NotBlank(message = "{error.password.notBlank}")
    @NotNull(message = "{error.password.notNull}")
    @Size(min = 8, max = 20, message = "{error.password.size}")
    private String password;

    private LocalDateTime lastAccountReactivationDate;
}
