package com.api.authentication.controllers;

import com.api.authentication.configurations.MessageProperty;
import com.api.authentication.dtos.AuthenticationDto;
import com.api.authentication.dtos.AuthenticationConfirmAccountDto;
import com.api.authentication.dtos.CreateUserDto;
import com.api.authentication.exception.AuthenticationOperationExceptionBadRequest;
import com.api.authentication.messages.MessagesSuccess;
import com.api.authentication.models.AuthenticationModel;
import com.api.authentication.repositories.AuthenticationRepository;
import com.api.authentication.services.AuthenticationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/authentication")
@SuppressWarnings("unused")
public class AuthenticationController {

    //Ponto de injeção com o service
    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Autowired
    MessageProperty messageProperty;

    @PostMapping
    public ResponseEntity<Object> createUser (@RequestBody @Valid AuthenticationDto authenticationDto, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            throw new AuthenticationOperationExceptionBadRequest(result.getFieldError().getDefaultMessage());
        }

        ResponseEntity<Object> validateUser = authenticationService.validateUser(authenticationDto);

        if(validateUser != null) {
            return validateUser;
        }

        AuthenticationModel authenticationModel = new AuthenticationModel();

        //Faz a conversão do dto em model passando o que vai ser convertido para o que está sendo convertido
        BeanUtils.copyProperties(authenticationDto, authenticationModel);

        authenticationModel.setExternalId(UUID.randomUUID().toString());
        authenticationModel.setCreatedDate(LocalDateTime.now());
        authenticationModel.setPassword(authenticationService.passwordEncoder(authenticationModel.getPassword()));

        authenticationService.saveUser(authenticationModel);

        CreateUserDto createUserDto = new CreateUserDto();

        createUserDto.setExternalId(authenticationModel.getExternalId());
        createUserDto.setCpfCnpj(authenticationModel.getCpfCnpj());
        createUserDto.setEmail(authenticationModel.getEmail());
        createUserDto.setName(authenticationModel.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(createUserDto);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody @Valid AuthenticationConfirmAccountDto authenticationConfirmAccountDto, BindingResult result) throws Exception {
        if(result.hasErrors()) {
            throw new AuthenticationOperationExceptionBadRequest(result.getFieldError().getDefaultMessage());
        }

        return authenticationService.login(authenticationConfirmAccountDto);
    }

    //Rota desenvolvida para aprendizado
    @GetMapping("/listAllUserNoPages")
    public ResponseEntity<List<AuthenticationModel>> listAllUserNoPages() {
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.findAllNoPages());
    }

    //Rota desenvolvida para aprendizado
    @GetMapping("/listAllUserWithPages")
    public ResponseEntity<Page<AuthenticationModel>> listAllUserWithPages(@PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.findAllWithPages(pageable));
    }

    @GetMapping("/{externalId}")
    public ResponseEntity<AuthenticationModel> listOneUser(@PathVariable(value = "externalId") @NotNull String externalId) {
        return authenticationService.findById(externalId).map(rec -> ResponseEntity.ok().body(rec))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());

    }

    @PutMapping("/resetPassword")
    public ResponseEntity<Object> editUser(@RequestBody @Valid AuthenticationConfirmAccountDto authenticationConfirmAccountDto, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            throw new AuthenticationOperationExceptionBadRequest(result.getFieldError().getDefaultMessage());
        }

        if(authenticationService.confirmEmail(authenticationConfirmAccountDto.getEmail())) {
            throw new AuthenticationOperationExceptionBadRequest(messageProperty.getProperty("error.email.notRegistered"));
        }

        AuthenticationModel authenticationModel =  authenticationRepository.findByEmail(authenticationConfirmAccountDto.getEmail());

        authenticationConfirmAccountDto.setPassword(authenticationService.passwordEncoder(authenticationConfirmAccountDto.getPassword()));

        BeanUtils.copyProperties(authenticationConfirmAccountDto, authenticationModel);
        authenticationModel.setLastUpdateDate(LocalDateTime.now());

        authenticationService.saveNewPassword(authenticationModel);

        MessagesSuccess success = new MessagesSuccess(messageProperty.getProperty("ok.password.changed"), HttpStatus.OK.value());

        return ResponseEntity.status(HttpStatus.OK).body(success);
    }

    @DeleteMapping()
    public ResponseEntity<Object> deleteUser(@RequestBody @Valid AuthenticationConfirmAccountDto authenticationConfirmAccountDto, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            throw new AuthenticationOperationExceptionBadRequest(result.getFieldError().getDefaultMessage());
        }

        if(authenticationService.confirmEmail(authenticationConfirmAccountDto.getEmail())) {
            throw new AuthenticationOperationExceptionBadRequest(messageProperty.getProperty("error.account.notRegistered"));
        }

        AuthenticationModel authenticationModel =  authenticationRepository.findByEmail(authenticationConfirmAccountDto.getEmail());

        if(!authenticationModel.isUserStatus()) {
            throw new AuthenticationOperationExceptionBadRequest(messageProperty.getProperty("error.account.notRegistered"));
        }

        if(BCrypt.checkpw(authenticationConfirmAccountDto.getPassword(), authenticationModel.getPassword())){
            authenticationModel.setUserStatus(false);
            authenticationModel.setLastDeleteDate(LocalDateTime.now());
            authenticationService.delete(authenticationModel);
            MessagesSuccess success = new MessagesSuccess(messageProperty.getProperty("ok.delete.user"),HttpStatus.OK.value());

            return ResponseEntity.status(HttpStatus.OK).body(success);
        }

        throw new AuthenticationOperationExceptionBadRequest(messageProperty.getProperty("error.password.incorrect"));
    }

}
