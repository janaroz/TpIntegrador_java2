package com.alkemy.java2.TPIntegrador.service.impl;

import com.alkemy.java2.TPIntegrador.DTOs.EventDTO;
import com.alkemy.java2.TPIntegrador.DTOs.ParticipantDTO;
import com.alkemy.java2.TPIntegrador.mappers.GenericMapper;
import com.alkemy.java2.TPIntegrador.repository.EventRepository;
import com.alkemy.java2.TPIntegrador.service.EventService;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;



import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository repo;
    private final GenericMapper mapper;

    private final ExecutorService executor = new DelegatingSecurityContextExecutorService(Executors.newFixedThreadPool(4));
    @Override
    public EventDTO createEvent(EventDTO dto) {
        System.out.println("[CREATE] Iniciando creación de evento...");
        EventDTO result = mapper.toDTO(
                repo.save(mapper.toEntity(dto, com.alkemy.java2.TPIntegrador.model.Event.class)),
                EventDTO.class);
        System.out.println("[CREATE] Evento creado con ID: " + result.getId());
        return result;
    }

    @Override
    public EventDTO getEventById(String id) {
        System.out.println("[GET] Buscando evento por ID: " + id);
        return repo.findById(id)
                .map(event -> {
                    System.out.println("[GET] Evento encontrado: " + event.getTitle());
                    return mapper.toDTO(event, EventDTO.class);
                })
                .orElse(null);
    }

    @Override
    public List<EventDTO> getEventsByGroup(String groupId) {
        System.out.println("[GROUP] Buscando eventos del grupo: " + groupId);
        List<EventDTO> result = repo.findByGroupId(groupId)
                .stream()
                .map(event -> mapper.toDTO(event, EventDTO.class))
                .collect(toList());
        System.out.println("[GROUP] Cantidad de eventos encontrados: " + result.size());
        return result;
    }

    // ✅ Async con log
    @Override
    public CompletableFuture<List<EventDTO>> getEventsByGroupAsync(String groupId) {
        SecurityContext context = SecurityContextHolder.getContext();

        System.out.println("[ASYNC] Iniciando búsqueda async para grupo: " + groupId);
        return CompletableFuture.supplyAsync(() -> {
            SecurityContextHolder.setContext(context);
            System.out.println("[ASYNC] Ejecutando en hilo: " + Thread.currentThread().getName());
            List<EventDTO> eventos = getEventsByGroup(groupId);
            System.out.println("[ASYNC] Completado para grupo: " + groupId);
            return eventos;
        }, executor);
    }

    @Override
    public List<EventDTO> getEventsByCreator(String creatorId) {
        System.out.println("[CREATOR] Buscando eventos creados por usuario: " + creatorId);
        List<EventDTO> result = repo.findByCreatorId(creatorId)
                .stream()
                .map(event -> mapper.toDTO(event, EventDTO.class))
                .collect(toList());
        System.out.println("[CREATOR] Cantidad de eventos encontrados: " + result.size());
        return result;
    }

    @Override
    public EventDTO updateEvent(String id, EventDTO dto) {
        System.out.println("[UPDATE] Buscando evento para actualizar: " + id);
        return repo.findById(id).map(e -> {
            e.setTitle(dto.getTitle());
            e.setDescription(dto.getDescription());
            e.setEventDate(dto.getEventDate());
            e.setLocation(dto.getLocation());
            EventDTO updated = mapper.toDTO(repo.save(e), EventDTO.class);
            System.out.println("[UPDATE] Evento actualizado con ID: " + updated.getId());
            return updated;
        }).orElse(null);
    }

    @Override
    public void deleteEvent(String id) {
        System.out.println("[DELETE] Eliminando evento con ID: " + id);
        repo.deleteById(id);
        System.out.println("[DELETE] Evento eliminado.");
    }

    @Override
    public EventDTO addParticipant(String id, ParticipantDTO p) {
        System.out.println("[PARTICIPANT] Agregando participante al evento: " + id);
        return repo.findById(id).map(e -> {
            e.getParticipants().add(
                    mapper.toEntity(p, com.alkemy.java2.TPIntegrador.model.Participant.class));
            EventDTO updated = mapper.toDTO(repo.save(e), EventDTO.class);
            System.out.println("[PARTICIPANT] Participante agregado. Total ahora: " + updated.getParticipants().size());
            return updated;
        }).orElse(null);
    }

    @Override
    public void processEventsByMultipleGroups(List<String> groupIds) {
        System.out.println("[PROCESS] Procesando múltiples grupos...");
        for (String groupId : groupIds) {
            executor.submit(() -> {
                System.out.println("[PROCESS] Iniciando grupo: " + groupId + " en hilo: " + Thread.currentThread().getName());
                List<EventDTO> eventos = getEventsByGroup(groupId);
                System.out.println("[PROCESS] Grupo " + groupId + " tiene " + eventos.size() + " eventos.");
            });
        }
        System.out.println("[PROCESS] Envío de tareas al pool completado.");
    }

    @PreDestroy
    public void shutdownExecutor() {
        System.out.println("[SHUTDOWN] Cerrando pool de hilos...");
        executor.shutdown();
        System.out.println("[SHUTDOWN] Pool cerrado.");
    }
}
