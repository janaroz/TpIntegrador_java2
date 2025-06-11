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

@Document(collection = "groups")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Group {

    @Id
    private String id;

    @NotBlank
    private String name;

    private String description;

    @Builder.Default
    private boolean isPublic = true;

    @NotBlank
    private String ownerId;

    @Builder.Default
    private List<String> memberIds = List.of();

    @Builder.Default
    private Instant createdAt = Instant.now();
}
