package com.alkemy.java2.TPIntegrador.service.impl;

import com.alkemy.java2.TPIntegrador.DTOs.UserDTO;
import com.alkemy.java2.TPIntegrador.exceptions.ResourceNotFoundException;
import com.alkemy.java2.TPIntegrador.mappers.GenericMapper;
import com.alkemy.java2.TPIntegrador.model.User;
import com.alkemy.java2.TPIntegrador.repository.UserRepository;
import com.alkemy.java2.TPIntegrador.service.UserService;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository repo;
    private final GenericMapper mapper;

    // ✅ Pool de hilos con seguridad delegada
    private final ExecutorService executor =
            new DelegatingSecurityContextExecutorService(Executors.newFixedThreadPool(4));

    @Override
    public UserDTO createUser(UserDTO dto) {
        User user = mapper.toEntity(dto, User.class);
        if (repo.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        repo.save(user);
        return mapper.toDTO(user, UserDTO.class);
    }

    @Override
    public UserDTO getUserById(String id) {
        return repo.findById(id)
                .map(user -> mapper.toDTO(user, UserDTO.class))
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return repo.findAll()
                .stream()
                .map(user -> mapper.toDTO(user, UserDTO.class))
                .collect(toList());
    }

    // ✅ Versión async
    @Override
    public CompletableFuture<List<UserDTO>> getAllUsersAsync() {
        log.info("[ASYNC] Ejecutando getAllUsersAsync");
        return CompletableFuture.supplyAsync(() -> {
            log.info("[ASYNC] Hilo: {}", Thread.currentThread().getName());
            return getAllUsers();
        }, executor);
    }

    // ✅ Procesamiento paralelo
    @Override
    public void processUsersByIdList(List<String> ids) {
        log.info("[THREAD] Iniciando procesamiento paralelo de usuarios");
        for (String id : ids) {
            executor.submit(() -> {
                try {
                    UserDTO user = getUserById(id);
                    log.info("[THREAD] Usuario procesado: {} - {}", id, user.getUsername());
                } catch (ResourceNotFoundException e) {
                    log.warn("[THREAD] Usuario no encontrado: {}", id);
                }
            });
        }
    }

    @Override
    public UserDTO updateUser(UserDTO dto) {
        return repo.findById(dto.getId()).map(u -> {
            u.setFullName(dto.getFullName());
            u.setProfileImageUrl(dto.getProfileImageUrl());
            u.setEmail(dto.getEmail());
            return mapper.toDTO(repo.save(u), UserDTO.class);
        }).orElse(null);
    }

    @Override
    public void deleteUser(String id) {
        repo.deleteById(id);
    }

    @PreDestroy
    public void shutdownExecutor() {
        log.info("[SHUTDOWN] Cerrando pool de hilos de UserService...");
        executor.shutdown();
    }
}
