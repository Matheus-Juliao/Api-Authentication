package com.api.authentication.models;

import jakarta.persistence.*;

//utilizei o Getter e o Setter do lombok e não o @Data afins de aprendizado
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "users")
public class AuthenticationModel implements Serializable {

    //Com relação ao serialVersionUID, ele faz conversões de objeto Java para 'bytes' para serem salvos no banco de dados feitos pela JVM
    private  static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", length = 45, nullable = false, unique = true)
    private String externalId;

    @Column(name = "cpf_cnpj", nullable = false, unique = true, length = 18)
    private String cpfCnpj;

    @Column(nullable = false, length = 50, unique = true)
    private String email;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "last_update_date")
    private LocalDateTime lastUpdateDate;

    @Column(name = "user_status", nullable = false)
    private boolean userStatus = true;

}
