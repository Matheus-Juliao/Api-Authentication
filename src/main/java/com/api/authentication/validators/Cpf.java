package com.api.authentication.validators;

import lombok.Data;
import org.hibernate.validator.constraints.br.CPF;

@Data
public class Cpf {
    @CPF(message = "invalid")
    private String cpf;
}