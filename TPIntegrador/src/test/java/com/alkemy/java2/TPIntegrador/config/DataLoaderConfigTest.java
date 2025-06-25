package com.alkemy.java2.TPIntegrador.config;

import com.alkemy.java2.TPIntegrador.authSecurity.service.AuthServiceImpl;
import com.alkemy.java2.TPIntegrador.DTOs.UserRegisterDTO;
import com.alkemy.java2.TPIntegrador.model.Group;
import com.alkemy.java2.TPIntegrador.model.User;
import com.alkemy.java2.TPIntegrador.repository.EventRepository;
import com.alkemy.java2.TPIntegrador.repository.GroupRepository;
import com.alkemy.java2.TPIntegrador.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DataLoaderConfigTest {

    private AuthServiceImpl authService;
    private UserRepository userRepository;
    private GroupRepository groupRepository;
    private EventRepository eventRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private DataLoaderConfig dataLoaderConfig;

    @BeforeEach
    void setUp() {
        authService = mock(AuthServiceImpl.class);
        userRepository = mock(UserRepository.class);
        groupRepository = mock(GroupRepository.class);
        eventRepository = mock(EventRepository.class);
        passwordEncoder = mock(BCryptPasswordEncoder.class);
        dataLoaderConfig = new DataLoaderConfig(authService, userRepository, groupRepository, eventRepository, passwordEncoder);
    }

    @Test
    void testRunLoadsDataAndDeletesAll() {
        // Arrange
        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.of(User.builder().id("1").username("juancito").build()))
                .thenReturn(Optional.of(User.builder().id("2").username("flor").build()))
                .thenReturn(Optional.of(User.builder().id("3").username("matimessi").build()))
                .thenReturn(Optional.of(User.builder().id("4").username("lauu").build()));

        // Mock para grupos
        when(groupRepository.save(any())).thenAnswer(invocation -> {
            // Devuelve el mismo grupo pero con un nombre válido
            return Group.builder()
                    .id("g1")
                    .name("GrupoMock")
                    .description("desc")
                    .ownerId("1")
                    .memberIds(java.util.List.of("2", "3"))
                    .createdAt(java.time.Instant.now())
                    .build();
        });

        // Mock para eventos
        when(eventRepository.save(any())).thenAnswer(invocation -> {
            return com.alkemy.java2.TPIntegrador.model.Event.builder()
                    .id("e1")
                    .title("EventoMock")
                    .groupId("g1")
                    .organizerId("1")
                    .eventDate(java.time.LocalDateTime.now())
                    .createdAt(java.time.Instant.now())
                    .build();
        });

        // Act & Assert
        assertDoesNotThrow(() -> dataLoaderConfig.run());

        // Verificaciones
        verify(userRepository, times(1)).deleteAll();
        verify(groupRepository, times(1)).deleteAll();
        verify(eventRepository, times(1)).deleteAll();
        verify(authService, times(4)).register(any(UserRegisterDTO.class));
        verify(userRepository, times(4)).findByUsername(anyString());
        verify(groupRepository, atLeastOnce()).save(any());
        verify(eventRepository, atLeastOnce()).save(any());
    }
}