package com.alkemy.java2.TPIntegrador.authSecutiry.service;

import com.alkemy.java2.TPIntegrador.DTOs.AuthRequest;
import com.alkemy.java2.TPIntegrador.DTOs.AuthResponse;
import com.alkemy.java2.TPIntegrador.DTOs.UserRegisterDTO;
import com.alkemy.java2.TPIntegrador.authSecurity.service.AuthServiceImpl;
import com.alkemy.java2.TPIntegrador.authSecurity.service.JwtService;
import com.alkemy.java2.TPIntegrador.mappers.GenericMapper;
import com.alkemy.java2.TPIntegrador.model.User;
import com.alkemy.java2.TPIntegrador.model.enums.Role;
import com.alkemy.java2.TPIntegrador.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private GenericMapper genericMapper;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_validUser_returnsToken() {
        // Arrange
        UserRegisterDTO dto = UserRegisterDTO.builder()
                .username("user")
                .fullName("Nombre Apellido")
                .email("user@email.com")
                .password("password123")
                .role(Set.of(Role.USER))
                .profileImageUrl("img.jpg")
                .build();
        User user = new User();
        user.setUsername("user");
        user.setPasswordHash("hash");
        user.setRoles(Set.of(Role.USER));
        User savedUser = new User();
        savedUser.setId("1");
        when(userRepository.existsByUsername("user")).thenReturn(false);
        when(genericMapper.toEntity(dto, User.class)).thenReturn(user);
        when(passwordEncoder.encode("password123")).thenReturn("hash");
        when(userRepository.save(user)).thenReturn(savedUser);
        when(jwtService.generateToken(any())).thenReturn("jwt-token");

        // Act
        AuthResponse response = authService.register(dto);

        // Assert
        assertEquals("jwt-token", response.getToken());
        verify(userRepository).save(user);
    }

    @Test
    void register_existingUser_doesNotThrow_returnsToken() {
        // Arrange
        UserRegisterDTO dto = UserRegisterDTO.builder()
                .username("user")
                .fullName("Nombre Apellido")
                .email("user@email.com")
                .password("password123")
                .role(Set.of(Role.USER))
                .profileImageUrl("img.jpg")
                .build();
        User user = new User();
        user.setUsername("user");
        user.setPasswordHash("hash");
        user.setRoles(Set.of(Role.USER));
        User savedUser = new User();
        savedUser.setId("1");
        when(userRepository.existsByUsername("user")).thenReturn(true);
        when(genericMapper.toEntity(dto, User.class)).thenReturn(user);
        when(passwordEncoder.encode("password123")).thenReturn("hash");
        when(userRepository.save(user)).thenReturn(savedUser);
        when(jwtService.generateToken(any())).thenReturn("jwt-token");

        // Act
        AuthResponse response = authService.register(dto);

        // Assert
        assertEquals("jwt-token", response.getToken());
        verify(userRepository).save(user);
    }

    @Test
    void authenticate_validCredentials_returnsToken() {
        // Arrange
        AuthRequest request = AuthRequest.builder().username("user").password("password123").build();
        User user = new User();
        user.setUsername("user");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        // Act
        AuthResponse response = authService.authenticate(request);

        // Assert
        assertEquals("jwt-token", response.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void authenticate_invalidCredentials_throwsBadCredentials() {
        // Arrange
        AuthRequest request = AuthRequest.builder().username("user").password("badpass").build();
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> authService.authenticate(request));
    }

    @Test
    void authenticate_userNotFound_throwsBadCredentials() {
        // Arrange
        AuthRequest request = AuthRequest.builder().username("user").password("password123").build();
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> authService.authenticate(request));
    }
}