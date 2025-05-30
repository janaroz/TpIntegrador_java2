package com.alkemy.java2.TPIntegrador.cofig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
     @Bean
     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
         http
             .authorizeHttpRequests(authz -> authz
                 .requestMatchers(
                     "/swagger-ui/**",
                     "/v3/api-docs/**",
                     "/swagger-resources/**",
                     "/webjars/**",
                     "/api/**" // <-- Permite acceso público a la API
                 ).permitAll()
                 .anyRequest().authenticated()
             )
             .csrf(csrf -> csrf.disable());
         return http.build();
     }


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}