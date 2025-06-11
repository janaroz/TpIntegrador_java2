package com.alkemy.java2.TPIntegrador.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    private String id;

    @NotBlank
    private String title;

    private String description;

    private String groupId;

    private String creatorId;

    private String location;

    private Instant eventDate;

    private String organizerId;

    @Builder.Default
    private Instant createdAt = Instant.now();

    @Builder.Default
    private List<Participant> participants = List.of();



}
