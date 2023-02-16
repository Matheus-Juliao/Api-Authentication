package com.api.authentication.repositories;

import com.api.authentication.models.AuthenticationModel;
import com.api.authentication.models.EmailModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EmailRepository extends JpaRepository<EmailModel, Long>{

    @Query(
            value = "SELECT * FROM email u WHERE u.email_to = :email",
            nativeQuery = true)
    Optional<EmailModel> findByEmail(String email);
}
