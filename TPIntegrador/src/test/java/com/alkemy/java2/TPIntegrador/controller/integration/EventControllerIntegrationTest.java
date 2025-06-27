package com.alkemy.java2.TPIntegrador.controller.integration;


import com.alkemy.java2.TPIntegrador.DTOs.EventDTO;
import com.alkemy.java2.TPIntegrador.model.enums.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class EventControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    private EventDTO sampleEvent;

    @BeforeEach
    void setup() {
        sampleEvent = new EventDTO();
        sampleEvent.setTitle("Concierto en el Obelisco");
        sampleEvent.setDescription("Una fiesta popular");
        sampleEvent.setGroupId("group123");
        sampleEvent.setCreatorId("user456");
        sampleEvent.setLocation("Buenos Aires");
        sampleEvent.setEventDate(LocalDateTime.now().plusDays(10));
    }

    @WithMockUser(username = "user456", roles = {"USER"})
    @Test
    void shouldCreateAndGetEvent() throws Exception {
        String json = objectMapper.writeValueAsString(sampleEvent);

        // Crear evento
        String response = mockMvc.perform(post("/api/events")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        EventDTO created = objectMapper.readValue(response, EventDTO.class);

        // Obtener por ID
        mockMvc.perform(get("/api/events/{id}", created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(created.getId()));
    }

    @WithMockUser(username = "user456", roles = {"USER"})
    @Test
    void shouldUpdateEvent() throws Exception {
        // Crear
        String json = objectMapper.writeValueAsString(sampleEvent);
        String createdJson = mockMvc.perform(post("/api/events")
                        .contentType("application/json")
                        .content(json))
                .andReturn().getResponse().getContentAsString();
        EventDTO created = objectMapper.readValue(createdJson, EventDTO.class);

        // Modificar
        created.setTitle("Concierto actualizado");
        String updateJson = objectMapper.writeValueAsString(created);

        mockMvc.perform(put("/api/events/{id}", created.getId())
                        .contentType("application/json")
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Concierto actualizado"));
    }

    @WithMockUser(username = "user456", roles = {"USER"})
    @Test
    void shouldDeleteEvent() throws Exception {
        String json = objectMapper.writeValueAsString(sampleEvent);
        String createdJson = mockMvc.perform(post("/api/events")
                        .contentType("application/json")
                        .content(json))
                .andReturn().getResponse().getContentAsString();
        EventDTO created = objectMapper.readValue(createdJson, EventDTO.class);

        mockMvc.perform(delete("/api/events/{id}", created.getId()))
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "user456", roles = {"USER"})
    @Test
    void shouldAddParticipant() throws Exception {
        String json = objectMapper.writeValueAsString(sampleEvent);
        String createdJson = mockMvc.perform(post("/api/events")
                        .contentType("application/json")
                        .content(json))
                .andReturn().getResponse().getContentAsString();
        EventDTO created = objectMapper.readValue(createdJson, EventDTO.class);

        var participant = new com.alkemy.java2.TPIntegrador.DTOs.ParticipantDTO("user789", Status.CONFIRMED);
        String pJson = objectMapper.writeValueAsString(participant);

        mockMvc.perform(post("/api/events/{id}/participants", created.getId())
                        .contentType("application/json")
                        .content(pJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.participants[0].userId").value("user789"));
    }
}
