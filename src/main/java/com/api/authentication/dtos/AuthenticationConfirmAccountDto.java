package com.api.authentication.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthenticationConfirmAccountDto {
    @NotBlank(message = "{error.email.notBlank}")
    @NotNull(message = "{error.email.notNull}")
    @Size(max = 50, message = "{error.email.size}")
    @Email(message = "{error.email.invalid}")
    private String email;

    @NotBlank(message = "{error.password.notBlank}")
    @NotNull(message = "{error.password.notNull}")
    @Size(min = 8, max = 20, message = "{error.password.size}")
    private String password;

    private String token;
}
