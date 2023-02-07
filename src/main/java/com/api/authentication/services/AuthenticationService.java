package com.api.authentication.services;

import com.api.authentication.configurations.MessageProperty;
import com.api.authentication.dtos.AuthenticationConfirmAccountDto;
import com.api.authentication.dtos.AuthenticationDto;
import com.api.authentication.dtos.CreateUserDto;
import com.api.authentication.dtos.ErrorValidationDto;
import com.api.authentication.exception.AuthenticationOperationExceptionBadRequest;
import com.api.authentication.messages.MessagesSuccess;
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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;


@Service
public class AuthenticationService {

    //Ponto de injeçao com o repository
    @Autowired
    AuthenticationRepository authenticationRepository;

    @Autowired
    SecurityConfigurations securityConfigurations;

    @Autowired
    MessageProperty messageProperty;

    //Ações no banco de dados
    //@Transactional é usado quando temos relacionamentos que têem deleção ou salvamento em cascata, ele garante um rollback voltando tudo ao normal caso aconteca algum problema.
    @Transactional
    public AuthenticationModel saveUser(AuthenticationModel authenticationModel) {
        return authenticationRepository.save(authenticationModel);
    }

    public void saveNewPassword(AuthenticationModel authenticationModel) {
        authenticationRepository.save(authenticationModel);
    }

    public void delete(AuthenticationModel authenticationModel) {
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

    private String validByCpfCnpj(@NotNull String cpfCnpj) {
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

    public ResponseEntity<Object> validateUser(@NotNull AuthenticationDto authenticationDto) {
        //Retirando pontuação do cpfCnpj
        authenticationDto.setCpfCnpj(removeTheTagCpfCnpj(authenticationDto.getCpfCnpj()));

        ErrorValidationDto errorValidationDto = new ErrorValidationDto(new Date(), 400);

        //Verificando se o cpfCnpj da conta já existiu para reativação do mesmo
        if(existByCpfCnpj(authenticationDto.getCpfCnpj())) {
            AuthenticationModel authenticationModel = authenticationRepository.findBycpfCnpj(authenticationDto.getCpfCnpj());

            //Verifica se o novo email passado e o email cadastrado se diferem e caso for sim, verifica se não existe no banco de dados
            if(!authenticationDto.getEmail().equals(authenticationModel.getEmail())) {
                if(authenticationRepository.existsByEmail(authenticationDto.getEmail())) {
                    errorValidationDto.setField("email");
                    errorValidationDto.setMessage(messageProperty.getProperty("error.email.already.account"));
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorValidationDto);
                }
            }

            //Verificando se a conta existente estava desativada para reativar se não a conta não pode ser criada
            if(!authenticationModel.isUserStatus()) {
                authenticationDto.setLastAccountReactivationDate(LocalDateTime.now());
                authenticationDto.setEmail(authenticationDto.getEmail());
                authenticationDto.setPassword(passwordEncoder(authenticationDto.getPassword()));
                authenticationModel.setExternalId(UUID.randomUUID().toString());
                authenticationModel.setUserStatus(true);

                //Faz a conversão do dto em model passando o que vai ser convertido para o que está sendo convertido
                BeanUtils.copyProperties(authenticationDto, authenticationModel);

                authenticationModel = saveUser(authenticationModel);

                CreateUserDto createUserDto = new CreateUserDto(
                        authenticationModel.getExternalId(),
                        authenticationModel.getCpfCnpj(),
                        authenticationModel.getEmail(),
                        authenticationModel.getName()
                );

                return ResponseEntity.status(HttpStatus.OK).body(createUserDto);
            }

            errorValidationDto.setField("cpfCnpj");
            errorValidationDto.setMessage(messageProperty.getProperty("error.account.already"));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorValidationDto);
        }

        if(validByCpfCnpj(authenticationDto.getCpfCnpj()).compareTo("invalid") == 0) {
            errorValidationDto.setField("cpfCnpj");
            errorValidationDto.setMessage(messageProperty.getProperty("error.cpfCnpj.invalid"));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorValidationDto);
        }

        if(existsByEmail(authenticationDto.getEmail())) {
            errorValidationDto.setField("email");
            errorValidationDto.setMessage(messageProperty.getProperty("error.email.already.account"));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorValidationDto);
        }

        return null;
    }

    public boolean confirmEmail (String email) {
        return authenticationRepository.existsByEmail(email);
    }




    //Ações na autenticação

    public ResponseEntity<Object> login(@NotNull AuthenticationConfirmAccountDto authenticationConfirmAccountDto) {
        if(!existsByEmail(authenticationConfirmAccountDto.getEmail())) {
            throw new AuthenticationOperationExceptionBadRequest(messageProperty.getProperty("error.account.notRegistered"));
        }

        AuthenticationModel authenticationModel = authenticationRepository.findByEmail(authenticationConfirmAccountDto.getEmail());

        if(!authenticationModel.isUserStatus()) {
            throw new AuthenticationOperationExceptionBadRequest(messageProperty.getProperty("error.account.notRegistered"));
        }

        if(BCrypt.checkpw(authenticationConfirmAccountDto.getPassword(), authenticationModel.getPassword())) {
            MessagesSuccess success = new MessagesSuccess(messageProperty.getProperty("ok.login.success"), HttpStatus.OK.value());

            return ResponseEntity.status(HttpStatus.OK).body(success);
        }

        MessagesSuccess success = new MessagesSuccess(messageProperty.getProperty("error.unauthorized"), HttpStatus.UNAUTHORIZED.value());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(success);
    }

    @Contract(pure = true)
    private @NotNull String removeTheTagCpfCnpj (@NotNull String cpfCnpj) {
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
