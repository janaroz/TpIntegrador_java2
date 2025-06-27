package com.alkemy.java2.TPIntegrador.authSecutiry.controller;

import com.alkemy.java2.TPIntegrador.DTOs.UserRegisterDTO;
import com.alkemy.java2.TPIntegrador.DTOs.AuthRequest;

import com.alkemy.java2.TPIntegrador.model.enums.Role;
import com.alkemy.java2.TPIntegrador.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    void shouldRegisterNewUser() throws Exception {
        var userRegister = UserRegisterDTO.builder()
                .fullName("Juan Pérez")
                .username("juanp")
                .email("juan@mail.com")
                .password("password123")
                .role(Set.of(Role.USER))
                .profileImageUrl("profile.jpg")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegister)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void shouldNotRegisterDuplicateUsername() throws Exception {
        var userRegister = UserRegisterDTO.builder()
                .fullName("Juan Pérez")
                .username("juanp")
                .email("juan@mail.com")
                .password("password123")
                .role(Set.of(Role.USER))
                .profileImageUrl("profile.jpg")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegister)))
                .andExpect(status().isOk());

        // segundo intento, mismo username
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegister)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());

    }

    @Test
    void shouldLoginWithValidCredentials() throws Exception {
        var userRegister = UserRegisterDTO.builder()
                .fullName("Maria Gomez")
                .username("mariag")
                .email("maria@mail.com")
                .password("mariapass")
                .role(Set.of(Role.USER))
                .profileImageUrl("profile.jpg")
                .build();

        // Registramos primero
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegister)))
                .andExpect(status().isOk());

        var loginRequest = AuthRequest.builder()
                .username("mariag")
                .password("mariapass")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void shouldFailLoginWithWrongPassword() throws Exception {
        var userRegister = UserRegisterDTO.builder()
                .fullName("Carlos Lopez")
                .username("carlosl")
                .email("carlos@mail.com")
                .password("carlospass")
                .role(Set.of(Role.USER))
                .profileImageUrl("profile.jpg")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegister)))
                .andExpect(status().isOk());

        var loginRequest = AuthRequest.builder()
                .username("carlosl")
                .password("wrongpass")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
}