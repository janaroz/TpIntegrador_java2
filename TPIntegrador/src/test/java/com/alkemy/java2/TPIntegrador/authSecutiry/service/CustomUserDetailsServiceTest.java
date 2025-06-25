package com.alkemy.java2.TPIntegrador.authSecurity.service;

import com.alkemy.java2.TPIntegrador.model.User;
import com.alkemy.java2.TPIntegrador.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    @Mock private UserRepository userRepository;
    private CustomUserDetailsService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new CustomUserDetailsService(userRepository);
    }

    @Test
    void loadUserByUsername_userExists_returnsUser() {
        // Arrange
        User user = new User();
        user.setUsername("user");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        // Act
        var result = service.loadUserByUsername("user");

        // Assert
        assertEquals("user", result.getUsername());
    }

    @Test
    void loadUserByUsername_userNotFound_throwsException() {
        // Arrange
        when(userRepository.findByUsername("nouser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("nouser"));
    }
}