package com.alkemy.java2.TPIntegrador.controller;

import com.alkemy.java2.TPIntegrador.DTOs.EventDTO;
import com.alkemy.java2.TPIntegrador.DTOs.ParticipantDTO;
import com.alkemy.java2.TPIntegrador.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService svc;

    @PostMapping
    public EventDTO create(@RequestBody EventDTO dto) {
        return svc.createEvent(dto);
    }

    @GetMapping("/{id}")
    public EventDTO get(@PathVariable String id) {
        return svc.getEventById(id);
    }

    @GetMapping("/group/{groupId}")
    public List<EventDTO> byGroup(@PathVariable String groupId) {
        return svc.getEventsByGroup(groupId);
    }

    // ✅ Nuevo endpoint asincrónico con CompletableFuture
    @GetMapping("/group-async/{groupId}")
    public CompletableFuture<List<EventDTO>> byGroupAsync(@PathVariable String groupId) {
        return svc.getEventsByGroupAsync(groupId);
    }

    @GetMapping("/creator/{userId}")
    public List<EventDTO> byCreator(@PathVariable String userId) {
        return svc.getEventsByCreator(userId);
    }

    @PutMapping("/{id}")
    public EventDTO update(@PathVariable String id, @RequestBody EventDTO dto) {
        return svc.updateEvent(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        svc.deleteEvent(id);
    }

    @PostMapping("/{id}/participants")
    public EventDTO addParticipant(@PathVariable String id, @RequestBody ParticipantDTO p) {
        return svc.addParticipant(id, p);
    }

    // ✅ Nuevo endpoint para ejecutar procesamiento paralelo
    @PostMapping("/process-groups")
    public String processGroups(@RequestBody List<String> groupIds) {
        svc.processEventsByMultipleGroups(groupIds);
        return "Procesamiento paralelo iniciado para los grupos: " + groupIds.size();
    }
}
