package com.alkemy.java2.TPIntegrador.controller;

import com.alkemy.java2.TPIntegrador.DTOs.EventDTO;
import com.alkemy.java2.TPIntegrador.DTOs.ParticipantDTO;
import com.alkemy.java2.TPIntegrador.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService svc;

    @PostMapping
    public EventDTO create(@RequestBody EventDTO dto) { return svc.createEvent(dto); }

    @GetMapping("/{id}")
    public EventDTO get(@PathVariable String id) { return svc.getEventById(id); }

    @GetMapping("/group/{groupId}")
    public List<EventDTO> byGroup(@PathVariable String groupId) {
        return svc.getEventsByGroup(groupId);
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
    public void delete(@PathVariable String id) { svc.deleteEvent(id); }

    @PostMapping("/{id}/participants")
    public EventDTO addParticipant(@PathVariable String id, @RequestBody ParticipantDTO p) {
        return svc.addParticipant(id, p);
    }
}

