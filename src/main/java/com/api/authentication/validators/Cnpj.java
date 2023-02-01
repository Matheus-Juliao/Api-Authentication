package com.api.authentication.validators;

import lombok.Data;
import org.hibernate.validator.constraints.br.CNPJ;

@Data
public class Cnpj {
    @CNPJ(message = "invalid")
    private String cnpj;
}