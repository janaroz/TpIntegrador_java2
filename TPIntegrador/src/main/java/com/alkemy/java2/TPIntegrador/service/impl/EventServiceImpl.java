package com.alkemy.java2.TPIntegrador.service.impl;

import com.alkemy.java2.TPIntegrador.DTOs.EventDTO;
import com.alkemy.java2.TPIntegrador.DTOs.ParticipantDTO;
import com.alkemy.java2.TPIntegrador.mappers.GenericMapper;
import com.alkemy.java2.TPIntegrador.model.Participant;
import com.alkemy.java2.TPIntegrador.repository.EventRepository;
import com.alkemy.java2.TPIntegrador.service.EventService;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private static final Logger log = LoggerFactory.getLogger(EventServiceImpl.class);

    private final EventRepository repo;
    private final GenericMapper mapper;

    private final ExecutorService executor = new DelegatingSecurityContextExecutorService(Executors.newFixedThreadPool(4));
    @Override
    public EventDTO createEvent(EventDTO dto) {
        log.info("[CREATE] Iniciando creación de evento...");
        EventDTO result = mapper.toDTO(
                repo.save(mapper.toEntity(dto, com.alkemy.java2.TPIntegrador.model.Event.class)),
                EventDTO.class);
        log.info("[CREATE] Evento creado con ID: {}", result.getId());
        return result;
    }

    @Override
    public EventDTO getEventById(String id) {
        String safeId = com.alkemy.java2.TPIntegrador.util.LogSanitizer.sanitize(id);
        log.info("[GET] Buscando evento por ID: {}", safeId);
        return repo.findById(id)
                .map(event -> {
                    log.info("[GET] Evento encontrado: {}", event.getTitle());
                    return mapper.toDTO(event, EventDTO.class);
                })
                .orElse(null);
    }

    @Override
    public List<EventDTO> getEventsByGroup(String groupId) {
        String safeGroupId = com.alkemy.java2.TPIntegrador.util.LogSanitizer.sanitize(groupId);
        log.info("[GROUP] Buscando eventos del grupo: {}", safeGroupId);
        List<EventDTO> result = repo.findByGroupId(groupId)
                .stream()
                .map(event -> mapper.toDTO(event, EventDTO.class))
                .collect(toList());
        log.info("[GROUP] Cantidad de eventos encontrados: {}", result.size());
        return result;
    }

    @Override
    public CompletableFuture<List<EventDTO>> getEventsByGroupAsync(String groupId) {
        SecurityContext context = SecurityContextHolder.getContext();
        String safeGroupId = com.alkemy.java2.TPIntegrador.util.LogSanitizer.sanitize(groupId);

        log.info("[ASYNC] Iniciando búsqueda async para grupo: {}", safeGroupId);
        return CompletableFuture.supplyAsync(() -> {
            SecurityContextHolder.setContext(context);
            log.info("[ASYNC] Ejecutando en hilo: {}", Thread.currentThread().getName());
            List<EventDTO> eventos = getEventsByGroup(groupId);
            log.info("[ASYNC] Completado para grupo: {}", safeGroupId);
            return eventos;
        }, executor);
    }

    @Override
    public List<EventDTO> getEventsByCreator(String creatorId) {
        String safeCreatorId = com.alkemy.java2.TPIntegrador.util.LogSanitizer.sanitize(creatorId);
        log.info("[CREATOR] Buscando eventos creados por usuario: {}", safeCreatorId);
        List<EventDTO> result = repo.findByCreatorId(creatorId)
                .stream()
                .map(event -> mapper.toDTO(event, EventDTO.class))
                .collect(toList());
        log.info("[CREATOR] Cantidad de eventos encontrados: {}", result.size());
        return result;
    }

    @Override
    public EventDTO updateEvent(String id, EventDTO dto) {
        String safeId = com.alkemy.java2.TPIntegrador.util.LogSanitizer.sanitize(id);
        log.info("[UPDATE] Buscando evento para actualizar: {}", safeId);
        return repo.findById(id).map(e -> {
            e.setTitle(dto.getTitle());
            e.setDescription(dto.getDescription());
            e.setEventDate(dto.getEventDate());
            e.setLocation(dto.getLocation());
            EventDTO updated = mapper.toDTO(repo.save(e), EventDTO.class);
            log.info("[UPDATE] Evento actualizado con ID: {}", updated.getId());
            return updated;
        }).orElse(null);
    }

    @Override
    public void deleteEvent(String id) {
        String safeId = com.alkemy.java2.TPIntegrador.util.LogSanitizer.sanitize(id);
        log.info("[DELETE] Eliminando evento con ID: {}", safeId);
        repo.deleteById(id);
        log.info("[DELETE] Evento eliminado.");
    }

    @Override
    public EventDTO addParticipant(String id, ParticipantDTO p) {
        String safeId = com.alkemy.java2.TPIntegrador.util.LogSanitizer.sanitize(id);
        log.info("[PARTICIPANT] Agregando participante al evento: {}", safeId);
        return repo.findById(id).map(e -> {
            List<Participant> participantes =
                    e.getParticipants() == null ? new ArrayList<>() : new ArrayList<>(e.getParticipants());
            participantes.add(
                    mapper.toEntity(p, com.alkemy.java2.TPIntegrador.model.Participant.class)
            );
            e.setParticipants(participantes);

            EventDTO updated = mapper.toDTO(repo.save(e), EventDTO.class);
            log.info("[PARTICIPANT] Participante agregado. Total ahora: {}", updated.getParticipants().size());
            return updated;
        }).orElse(null);
    }

    @Override
    public void processEventsByMultipleGroups(List<String> groupIds) {
        log.info("[PROCESS] Procesando múltiples grupos...");
        for (String groupId : groupIds) {
            String safeGroupId = com.alkemy.java2.TPIntegrador.util.LogSanitizer.sanitize(groupId);
            executor.submit(() -> {
                log.info("[PROCESS] Iniciando grupo: {} en hilo: {}", safeGroupId, Thread.currentThread().getName());
                List<EventDTO> eventos = getEventsByGroup(groupId);
                log.info("[PROCESS] Grupo {} tiene {} eventos.", safeGroupId, eventos.size());
            });
        }
        log.info("[PROCESS] Envío de tareas al pool completado.");
    }

    @PreDestroy
    public void shutdownExecutor() {
        log.info("[SHUTDOWN] Cerrando pool de hilos...");
        executor.shutdown();
        log.info("[SHUTDOWN] Pool cerrado.");
    }
}