package com.alkemy.java2.TPIntegrador.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    private String id;
    private String title;
    private String description;
    private String groupId;
    private String creatorId;
    private String location;
    private LocalDateTime eventDate;
    private List<ParticipantDTO> participants;
}
