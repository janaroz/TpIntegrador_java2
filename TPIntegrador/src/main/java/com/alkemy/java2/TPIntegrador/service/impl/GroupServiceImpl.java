package com.alkemy.java2.TPIntegrador.service.impl;

import com.alkemy.java2.TPIntegrador.DTOs.GroupDTO;
import com.alkemy.java2.TPIntegrador.DTOs.UserDTO;
import com.alkemy.java2.TPIntegrador.mappers.GenericMapper;
import com.alkemy.java2.TPIntegrador.model.Group;
import com.alkemy.java2.TPIntegrador.repository.GroupRepository;
import com.alkemy.java2.TPIntegrador.service.GroupService;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final GenericMapper genericMapper;

    // ✅ Executor con propagación de contexto de seguridad
    private final ExecutorService executor =
            new DelegatingSecurityContextExecutorService(Executors.newFixedThreadPool(4));

    public GroupServiceImpl(GroupRepository groupRepository, GenericMapper genericMapper) {
        this.groupRepository = groupRepository;
        this.genericMapper = genericMapper;
    }

    @Override
    public GroupDTO createGroup(GroupDTO groupDTO) {
        Group group = genericMapper.toEntity(groupDTO, Group.class);
        group = groupRepository.save(group);
        log.info("[CREATE] Grupo creado con ID: {}", com.alkemy.java2.TPIntegrador.util.LogSanitizer.sanitize(group.getId()));
        return genericMapper.toDTO(group, GroupDTO.class);
    }

    @Override
    public GroupDTO getGroupById(String id) {
        String safeId = com.alkemy.java2.TPIntegrador.util.LogSanitizer.sanitize(id);
        return groupRepository.findById(id)
                .map(group -> {
                    log.info("[GET] Grupo obtenido: {} (ID: {})", group.getName(), safeId);
                    return genericMapper.toDTO(group, GroupDTO.class);
                }).orElse(null);
    }

    @Override
    public List<GroupDTO> getAllGroups() {
        List<GroupDTO> result = groupRepository.findAll()
                .stream()
                .map(group -> genericMapper.toDTO(group, GroupDTO.class))
                .collect(Collectors.toList());
        log.info("[LIST] Se encontraron {} grupos", result.size());
        return result;
    }

    public CompletableFuture<List<GroupDTO>> getAllGroupsAsync() {
        log.info("[ASYNC] getAllGroupsAsync iniciado");
        return CompletableFuture.supplyAsync(() -> {
            log.info("[ASYNC] Ejecutando en hilo: {}", Thread.currentThread().getName());
            List<GroupDTO> result = getAllGroups();
            log.info("[ASYNC] Finalizado con {} grupos", result.size());
            return result;
        }, executor);
    }

    public void processMultipleGroups(List<String> ids) {
        log.info("[THREAD] Procesando grupos en paralelo");
        for (String id : ids) {
            String safeId = com.alkemy.java2.TPIntegrador.util.LogSanitizer.sanitize(id);
            executor.submit(() -> {
                log.info("[THREAD] Buscando grupo: {} en hilo: {}", safeId, Thread.currentThread().getName());
                GroupDTO group = getGroupById(id);
                if (group != null) {
                    log.info("[THREAD] Grupo {}: {}", safeId, group.getName());
                } else {
                    log.warn("[THREAD] Grupo {} no encontrado", safeId);
                }
            });
        }
    }

    @Override
    public GroupDTO addMemberToGroup(String groupId, UserDTO userDTO) {
        String safeGroupId = com.alkemy.java2.TPIntegrador.util.LogSanitizer.sanitize(groupId);
        String safeUserId = com.alkemy.java2.TPIntegrador.util.LogSanitizer.sanitize(userDTO.getId());
        Optional<Group> groupOpt = groupRepository.findById(groupId);
        if (groupOpt.isPresent()) {
            Group group = groupOpt.get();
            List<String> memberIds = group.getMemberIds() != null ?
                    new java.util.ArrayList<>(group.getMemberIds()) : new java.util.ArrayList<>();
            memberIds.add(userDTO.getId());
            group.setMemberIds(memberIds);
            group = groupRepository.save(group);
            log.info("[MEMBER] Agregado {} al grupo {}", safeUserId, safeGroupId);
            return genericMapper.toDTO(group, GroupDTO.class);
        }
        return null;
    }

    @PreDestroy
    public void shutdown() {
        log.info("[SHUTDOWN] Cerrando pool de hilos...");
        executor.shutdown();
    }
}
