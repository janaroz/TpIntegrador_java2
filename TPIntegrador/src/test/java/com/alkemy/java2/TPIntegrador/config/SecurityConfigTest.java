package com.alkemy.java2.TPIntegrador.config;

import com.alkemy.java2.TPIntegrador.authSecurity.jwt.JwtAuthFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SecurityConfigTest {

    private JwtAuthFilter jwtAuthFilter;
    private UserDetailsService userDetailsService;
    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        jwtAuthFilter = mock(JwtAuthFilter.class);
        userDetailsService = mock(UserDetailsService.class);
        securityConfig = new SecurityConfig(jwtAuthFilter, userDetailsService);
    }

    @Test
    void testPasswordEncoderBean() {
        // Arrange & Act
        PasswordEncoder encoder = securityConfig.passwordEncoder();

        // Assert
        assertNotNull(encoder);
        assertTrue(encoder.matches("test", encoder.encode("test")));
        assertFalse(encoder.matches("test", encoder.encode("other")));
    }

    @Test
    void testAuthProviderBean() {
        // Arrange & Act
        AuthenticationProvider provider = securityConfig.authProvider();

        // Assert
        assertNotNull(provider);
        assertTrue(provider instanceof DaoAuthenticationProvider);
    }


    @Test
    void testAuthManagerBean() throws Exception {
        // Arrange
        AuthenticationManager mockManager = mock(AuthenticationManager.class);
        AuthenticationConfiguration config = mock(AuthenticationConfiguration.class);
        when(config.getAuthenticationManager()).thenReturn(mockManager);

        // Act
        AuthenticationManager manager = securityConfig.authManager(config);

        // Assert
        assertNotNull(manager);
        assertEquals(mockManager, manager);
    }

    @Test
    void testSecurityFilterChainThrowsException() {
        // Arrange
        // HttpSecurity es complejo de mockear, aquí solo se prueba el manejo de excepción
        assertThrows(NullPointerException.class, () -> securityConfig.securityFilterChain(null));
    }
}