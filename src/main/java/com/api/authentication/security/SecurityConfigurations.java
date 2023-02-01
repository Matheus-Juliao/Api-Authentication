package com.api.authentication.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfigurations {

    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity htpp) throws Exception {
        htpp
                .httpBasic()
                .and()
                .authorizeHttpRequests()
//                .requestMatchers(HttpMethod.POST, "/api/authentication/login").permitAll()
                .anyRequest().permitAll()
                .and()
                .csrf().disable();

        return  htpp.build();
    }
}
