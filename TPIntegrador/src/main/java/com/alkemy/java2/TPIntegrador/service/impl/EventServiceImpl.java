package com.alkemy.java2.TPIntegrador.service.impl;

import com.alkemy.java2.TPIntegrador.DTOs.EventDTO;
import com.alkemy.java2.TPIntegrador.DTOs.ParticipantDTO;
import com.alkemy.java2.TPIntegrador.mappers.GenericMapper;
import com.alkemy.java2.TPIntegrador.repository.EventRepository;
import com.alkemy.java2.TPIntegrador.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository repo;
    private final GenericMapper mapper;

    public EventDTO createEvent(EventDTO dto) {
        return mapper.toDTO(repo.save(mapper.toEntity(dto, com.alkemy.java2.TPIntegrador.model.Event.class)), EventDTO.class);
    }
    public EventDTO getEventById(String id) {
        return repo.findById(id).map(event -> mapper.toDTO(event, EventDTO.class)).orElse(null);
    }
    public List<EventDTO> getEventsByGroup(String groupId) {
        return repo.findByGroupId(groupId).stream().map(event -> mapper.toDTO(event, EventDTO.class)).collect(toList());
    }
    public List<EventDTO> getEventsByCreator(String creatorId) {
        return repo.findByCreatorId(creatorId).stream().map(event -> mapper.toDTO(event, EventDTO.class)).collect(toList());
    }
    public EventDTO updateEvent(String id, EventDTO dto) {
        return repo.findById(id).map(e -> {
            e.setTitle(dto.getTitle());
            e.setDescription(dto.getDescription());
            e.setEventDate(dto.getEventDate());
            e.setLocation(dto.getLocation());
            return mapper.toDTO(repo.save(e), EventDTO.class);
        }).orElse(null);
    }
    public void deleteEvent(String id) { repo.deleteById(id); }
    public EventDTO addParticipant(String id, ParticipantDTO p) {
        return repo.findById(id).map(e -> {
            e.getParticipants().add(mapper.toEntity(p, com.alkemy.java2.TPIntegrador.model.Participant.class));
            return mapper.toDTO(repo.save(e), EventDTO.class);
        }).orElse(null);
    }
}