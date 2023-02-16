package com.api.authentication.controllers;

import com.api.authentication.configurations.MessageProperty;
import com.api.authentication.dtos.AuthenticationDto;
import com.api.authentication.dtos.AuthenticationConfirmAccountDto;
import com.api.authentication.dtos.CreateUserDto;
import com.api.authentication.dtos.EmailDto;
import com.api.authentication.exception.handler.AuthenticationOperationExceptionBadRequest;
import com.api.authentication.exception.handler.AuthenticationOperationExceptionNotFound;
import com.api.authentication.messages.MessagesSuccess;
import com.api.authentication.models.AuthenticationModel;
import com.api.authentication.repositories.AuthenticationRepository;
import com.api.authentication.services.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springdoc.core.annotations.ParameterObject;
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
import java.util.Objects;
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
    @Operation(summary = "Register user",  description = "Api for register a new user on the plataform")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = CreateUserDto.class)) }),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<Object> createUser (@RequestBody @Valid AuthenticationDto authenticationDto, @org.jetbrains.annotations.NotNull BindingResult result) throws AuthenticationOperationExceptionBadRequest {
        if (result.hasErrors()) {
            throw new AuthenticationOperationExceptionBadRequest(Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
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
    @Operation(summary = "Logs the user into the application", description = "API for a user to authenticate on the platform")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = MessagesSuccess.class)) }),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<Object> login(@RequestBody @Valid AuthenticationConfirmAccountDto authenticationConfirmAccountDto, @org.jetbrains.annotations.NotNull BindingResult result) throws AuthenticationOperationExceptionBadRequest {
        if(result.hasErrors()) {
            throw new AuthenticationOperationExceptionBadRequest(Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
        }

        return authenticationService.login(authenticationConfirmAccountDto);
    }

    //Rota desenvolvida para aprendizado
    @GetMapping("/listAllUserNoPages")
    @Operation(summary = "Returns all users without using pagination", description = "API to list all users and their information on the platform\n\nNote: This route is designed for learning")
    public ResponseEntity<List<AuthenticationModel>> listAllUserNoPages() {
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.findAllNoPages());
    }

    //Rota desenvolvida para aprendizado
    @GetMapping("/listAllUserWithPages")
    @Operation(summary = "Returns all users using pagination", description = "API to list all users and their information on the platform\n\nNote: This route is designed for learning")
    public ResponseEntity<Page<AuthenticationModel>> listAllUserWithPages(@ParameterObject @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.findAllWithPages(pageable));
    }

    //Rota desenvolvida para aprendizado
    @GetMapping("/{externalId}")
    @Operation(summary = "Returns a user", description = "API to fetch a user on the platform\n\nNote: This route is designed for learning")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = AuthenticationModel.class)) }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<AuthenticationModel> listOneUser(@PathVariable(value = "externalId") @NotNull String externalId) {

        AuthenticationModel authenticationModel = authenticationService.findById(externalId);

        if(authenticationModel != null) {
            return ResponseEntity.ok().body(authenticationService.findById(externalId));
        }

        throw new AuthenticationOperationExceptionNotFound(messageProperty.getProperty("error.notFound"));
    }

    @PostMapping("/sendEmailToken")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = MessagesSuccess.class)) }),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true)))
    })
    @Operation(summary = "Send token by email", description = "API to email token a user on the platform")
    public ResponseEntity<Object> sendEmailtoken(@RequestBody @Valid EmailDto emailDto, @org.jetbrains.annotations.NotNull BindingResult result) throws AuthenticationOperationExceptionBadRequest {
        if (result.hasErrors()) {
            throw new AuthenticationOperationExceptionBadRequest(Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
        }

        if(authenticationService.confirmEmail(emailDto.getEmail())) {
            AuthenticationModel authenticationModel = authenticationRepository.findByEmail(emailDto.getEmail());

            if(!authenticationModel.isUserStatus()) {
                throw new AuthenticationOperationExceptionBadRequest(messageProperty.getProperty("error.account.notRegistered"));
            }

            return authenticationService.sendEmail(authenticationModel);
        }

        throw new AuthenticationOperationExceptionBadRequest(messageProperty.getProperty("error.email.notRegistered"));

    }

    @PutMapping("/resetPassword")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = MessagesSuccess.class)) }),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true)))
    })
    @Operation(summary = "Reset a new password", description = "API to reset a new password for a user on the platform")
    public ResponseEntity<Object> editUser(@RequestBody @Valid AuthenticationConfirmAccountDto authenticationConfirmAccountDto, @org.jetbrains.annotations.NotNull BindingResult result) throws AuthenticationOperationExceptionBadRequest {
        if (result.hasErrors()) {
            throw new AuthenticationOperationExceptionBadRequest(Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
        }

        if(authenticationService.confirmEmail(authenticationConfirmAccountDto.getEmail())) {
            AuthenticationModel authenticationModel =  authenticationRepository.findByEmail(authenticationConfirmAccountDto.getEmail());

            if(!authenticationModel.isUserStatus()) {
                throw new AuthenticationOperationExceptionBadRequest(messageProperty.getProperty("error.account.notRegistered"));
            }

            //Validate token
            if(!authenticationService.confirmToken(authenticationConfirmAccountDto)) {
                throw new AuthenticationOperationExceptionBadRequest(messageProperty.getProperty("error.token.invalid"));
            }

            authenticationConfirmAccountDto.setPassword(authenticationService.passwordEncoder(authenticationConfirmAccountDto.getPassword()));

            BeanUtils.copyProperties(authenticationConfirmAccountDto, authenticationModel);
            authenticationModel.setLastUpdateDate(LocalDateTime.now());

            authenticationService.saveNewPassword(authenticationModel);

            MessagesSuccess success = new MessagesSuccess(messageProperty.getProperty("ok.password.changed"), HttpStatus.OK.value());

            return ResponseEntity.status(HttpStatus.OK).body(success);
        }

        throw new AuthenticationOperationExceptionBadRequest(messageProperty.getProperty("error.email.notRegistered"));

    }

    @DeleteMapping()
    @Operation(summary = "Delete a user's account", description = "API to delete a user account on the platform")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(hidden = true))),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<Object> deleteUser(@RequestBody @Valid AuthenticationConfirmAccountDto authenticationConfirmAccountDto, @org.jetbrains.annotations.NotNull BindingResult result) throws AuthenticationOperationExceptionBadRequest {
        if (result.hasErrors()) {
            throw new AuthenticationOperationExceptionBadRequest(Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
        }

        if(authenticationService.confirmEmail(authenticationConfirmAccountDto.getEmail())) {
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

        throw new AuthenticationOperationExceptionBadRequest(messageProperty.getProperty("error.account.notRegistered"));
    }

}
