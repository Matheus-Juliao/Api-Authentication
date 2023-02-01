package com.api.authentication.services;

import com.api.authentication.dtos.AuthenticationDto;
import com.api.authentication.dtos.ErrorValidationDto;
import com.api.authentication.models.AuthenticationModel;
import com.api.authentication.repositories.AuthenticationRepository;
import com.api.authentication.security.SecurityConfigurations;
import com.api.authentication.validators.Cnpj;
import com.api.authentication.validators.Cpf;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Service
public class AuthenticationService {

    //Ponto de injeçao com o repository
    @Autowired
    AuthenticationRepository authenticationRepository;

    @Autowired
    SecurityConfigurations securityConfigurations;

    //Salvando dados no banco
    //@Transactional é usado quando temos relacionamentos que têem deleção ou salvamento em cascata, ele garante um rollback voltando tudo ao normal caso aconteca algum problema.
    @Transactional
    public AuthenticationModel saveUser(AuthenticationModel authenticationModel) {
        return authenticationRepository.save(authenticationModel);
    }

    public void saveNewPassword(AuthenticationModel authenticationModel) {
        authenticationRepository.save(authenticationModel);
    }




    //Verificando se existe o parametro passado no banco de dados

    private boolean existByCpfCnpj(String cpfCnpj) {
        return authenticationRepository.existsByCpfCnpj(cpfCnpj);
    }

    private boolean existsByEmail(String email) {
        return authenticationRepository.existsByEmail(email);
    }




    //Validações

    private String validByCpfCnpj(String cpfCnpj) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        if(cpfCnpj.length() < 12) {
            Cpf cpf = new Cpf();
            cpf.setCpf(cpfCnpj);

            Set<ConstraintViolation<Cpf>> violations = validator.validate(cpf);

            for(ConstraintViolation<Cpf> violation : violations) {
                return violation.getMessage();
            }
        } else  {
            Cnpj cnpj = new Cnpj();
            cnpj.setCnpj(cpfCnpj);

            Set<ConstraintViolation<Cnpj>> violations = validator.validate(cnpj);

            for(ConstraintViolation<Cnpj> violation : violations) {
                return violation.getMessage();
            }

        }

        return "valid";
    }

    public ResponseEntity<Object> validateUser(AuthenticationDto authenticationDto) {
        //Retirando pontuação do cpfCnpj
        authenticationDto.setCpfCnpj(removeTheTagCpfCnpj(authenticationDto.getCpfCnpj()));

        ErrorValidationDto errorValidationDto = new ErrorValidationDto(new Date(), 400);

        if(existByCpfCnpj(authenticationDto.getCpfCnpj())) {
            errorValidationDto.setField("cpfCnpj");
            errorValidationDto.setMessage("cpfCnpj is already registered");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorValidationDto);
        }

        if(validByCpfCnpj(authenticationDto.getCpfCnpj()).compareTo("invalid") == 0) {
            errorValidationDto.setField("cpfCnpj");
            errorValidationDto.setMessage("cpfCnpj invalid!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorValidationDto);
        }

        if(existsByEmail(authenticationDto.getEmail())) {
            errorValidationDto.setField("email");
            errorValidationDto.setMessage("email is already registered");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorValidationDto);
        }

        return null;
    }

    public boolean validateResetPassword (String email) {
        return existsByEmail(email) ? true : false;
    }




    //Ações

    public ResponseEntity<String> login(AuthenticationDto authenticationDto) {
        String cpfCnpj = removeTheTagCpfCnpj(authenticationDto.getCpfCnpj());
        if(!existByCpfCnpj(cpfCnpj)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Cpf/Cnpj not registered!");
        }

        Optional<AuthenticationModel> authenticationModel = authenticationRepository.findBycpfCnpj(cpfCnpj);

        if(BCrypt.checkpw(authenticationDto.getPassword(), authenticationModel.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.OK).body("Login successfully!");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized!");
    }

    private String removeTheTagCpfCnpj (String cpfCnpj) {
        return  cpfCnpj.replaceAll("\\p{Punct}", "");
    }




    //Encontrar por...

    public List<AuthenticationModel> findAllNoPages() {
        return authenticationRepository.findAll();
    }

    public Page<AuthenticationModel> findAllWithPages(Pageable pageable) {
        return authenticationRepository.findAll(pageable);
    }

    public Optional<AuthenticationModel> findById(String externalId) {
        return authenticationRepository.findByExternalId(externalId);
    }




    //Cripitografia de senha

    public String passwordEncoder(String password) {
        return securityConfigurations.passwordEncoder().encode(password);
    }




}
