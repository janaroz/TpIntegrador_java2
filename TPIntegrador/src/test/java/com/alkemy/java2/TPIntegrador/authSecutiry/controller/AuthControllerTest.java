package com.alkemy.java2.TPIntegrador.authSecutiry.controller;

import com.alkemy.java2.TPIntegrador.DTOs.AuthRequest;
import com.alkemy.java2.TPIntegrador.DTOs.AuthResponse;
import com.alkemy.java2.TPIntegrador.DTOs.UserRegisterDTO;
import com.alkemy.java2.TPIntegrador.authSecurity.controller.AuthController;
import com.alkemy.java2.TPIntegrador.authSecurity.service.AuthService;
import com.alkemy.java2.TPIntegrador.model.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private AuthService authService;

    private AuthController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new AuthController(authService);
    }

    @Test
    void login_validRequest_returnsToken() {
        // Arrange
        AuthRequest request = AuthRequest.builder().username("user").password("password123").build();
        AuthResponse response = AuthResponse.builder().token("jwt-token").build();
        when(authService.authenticate(request)).thenReturn(response);

        // Act
        ResponseEntity<AuthResponse> result = controller.login(request);

        // Assert
        assertEquals(200, result.getStatusCodeValue());
        assertEquals("jwt-token", result.getBody().getToken());
        verify(authService).authenticate(request);
    }

    @Test
    void login_invalidCredentials_throwsException() {
        // Arrange
        AuthRequest request = AuthRequest.builder().username("user").password("wrongpass").build();
        when(authService.authenticate(request)).thenThrow(new RuntimeException("Invalid credentials"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> controller.login(request));
        verify(authService).authenticate(request);
    }

    @Test
    void register_validRequest_returnsToken() {
        // Arrange
        UserRegisterDTO request = UserRegisterDTO.builder()
                .username("user")
                .fullName("Nombre Apellido")
                .email("user@email.com")
                .password("password123")
                .role(Set.of(Role.USER))
                .profileImageUrl("http://img.com/img.jpg")
                .build();
        AuthResponse response = AuthResponse.builder().token("jwt-token").build();
        when(authService.register(request)).thenReturn(response);

        // Act
        ResponseEntity<AuthResponse> result = controller.register(request);

        // Assert
        assertEquals(200, result.getStatusCodeValue());
        assertEquals("jwt-token", result.getBody().getToken());
        verify(authService).register(request);
    }

    @Test
    void register_existingUser_returnsTokenOrHandlesError() {
        // Arrange
        UserRegisterDTO request = UserRegisterDTO.builder()
                .username("existing")
                .fullName("Nombre Apellido")
                .email("existing@email.com")
                .password("password123")
                .role(Set.of(Role.USER))
                .profileImageUrl("http://img.com/img.jpg")
                .build();
        AuthResponse response = AuthResponse.builder().token("jwt-token").build();
        when(authService.register(request)).thenReturn(response);

        // Act
        ResponseEntity<AuthResponse> result = controller.register(request);

        // Assert
        assertEquals(200, result.getStatusCodeValue());
        assertEquals("jwt-token", result.getBody().getToken());
        verify(authService).register(request);
    }

    @Test
    void register_invalidRequest_throwsException() {
        // Arrange
        UserRegisterDTO request = UserRegisterDTO.builder()
                .username("")
                .fullName("")
                .email("noemail")
                .password("")
                .role(Set.of())
                .profileImageUrl("")
                .build();
        when(authService.register(request)).thenThrow(new IllegalArgumentException("Invalid data"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> controller.register(request));
        verify(authService).register(request);
    }
}