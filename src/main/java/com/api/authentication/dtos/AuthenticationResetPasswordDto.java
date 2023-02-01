package com.api.authentication.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthenticationResetPasswordDto {
    @NotBlank
    @NotNull
    @Size(max = 20)
    private String password;

    @NotBlank
    @NotNull
    @Size(max = 50)
    private String email;
}
