package com.api.authentication.repositories;

import com.api.authentication.models.AuthenticationModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthenticationRepository extends JpaRepository<AuthenticationModel, Long> {

    boolean existsByCpfCnpj(String cpfCnpj);
    boolean existsByEmail(String email);


    @Query(
        value = "SELECT * FROM users u WHERE u.external_id = :externalId",
        nativeQuery = true)
    AuthenticationModel findByExternalId(String externalId);

    @Query(
            value = "SELECT * FROM users u WHERE u.cpf_cnpj = :cpfCnpj",
            nativeQuery = true)
    AuthenticationModel findBycpfCnpj(String cpfCnpj);

    @Query(
            value = "SELECT * FROM users u WHERE u.email = :email",
            nativeQuery = true)
     AuthenticationModel findByEmail(String email);

}
