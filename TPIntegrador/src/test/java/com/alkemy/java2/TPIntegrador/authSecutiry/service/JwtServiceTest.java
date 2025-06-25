package com.alkemy.java2.TPIntegrador.authSecutiry.service;

import com.alkemy.java2.TPIntegrador.authSecurity.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() throws Exception {
      jwtService = new JwtService();
      // Clave base64 de 32 bytes (256 bits)
      setPrivateField(jwtService, "secretKey", "ZmFrZXNlY3JldGtleWZha2VzZWNyZXRrZXlGQUtFU0VDUkVUS0VZRkFLRVNFQ1JFVEtFWQ==");
      setPrivateField(jwtService, "expirationMs", 10000L);
    }

    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void generateToken_and_validateToken() {
        // Arrange
        User user = new User("user", "pass", List.of(new SimpleGrantedAuthority("ROLE_USER")));

        // Act
        String token = jwtService.generateToken(user);

        // Assert
        assertNotNull(token);
        assertTrue(jwtService.isTokenValid(token, user));
    }

    @Test
    void extractUsername_returnsCorrectUsername() {
        // Arrange
        User user = new User("user", "pass", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        String token = jwtService.generateToken(user);

        // Act
        String username = jwtService.extractUsername(token);

        // Assert
        assertEquals("user", username);
    }

    @Test
    void isTokenValid_expiredToken_returnsFalse() throws Exception {
        // Arrange
        User user = new User("user", "pass", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        setPrivateField(jwtService, "expirationMs", 1L); // Expira casi de inmediato
        String token = jwtService.generateToken(user);
        Thread.sleep(5);

        // Act
        boolean valid = jwtService.isTokenValid(token, user);

        // Assert
        assertFalse(valid);
    }
}