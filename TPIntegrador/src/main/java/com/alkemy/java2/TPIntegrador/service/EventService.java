package com.alkemy.java2.TPIntegrador.service;

import com.alkemy.java2.TPIntegrador.DTOs.EventDTO;
import com.alkemy.java2.TPIntegrador.DTOs.ParticipantDTO;

import java.util.List;

public interface EventService {
    EventDTO createEvent(EventDTO dto);
    EventDTO getEventById(String id);
    List<EventDTO> getEventsByGroup(String groupId);
    List<EventDTO> getEventsByCreator(String creatorId);
    EventDTO updateEvent(String id, EventDTO dto);
    void deleteEvent(String id);
    EventDTO addParticipant(String id, ParticipantDTO p);
}
