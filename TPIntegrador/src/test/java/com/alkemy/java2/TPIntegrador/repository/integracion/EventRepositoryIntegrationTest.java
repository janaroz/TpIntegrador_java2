package com.alkemy.java2.TPIntegrador.repository.integracion;

import com.alkemy.java2.TPIntegrador.MongoDB.BaseMongoIntegrationTest;
import com.alkemy.java2.TPIntegrador.model.Event;
import com.alkemy.java2.TPIntegrador.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@Testcontainers
class EventRepositoryIntegrationTest extends BaseMongoIntegrationTest {

    @Autowired
    private EventRepository eventRepository;

    @Test
    void shouldSaveAndQueryByGroupAndCreatorAndDate() {
        Event event = Event.builder()
                .title("Reunión")
                .groupId("groupX")
                .creatorId("userX")
                .eventDate(LocalDateTime.now())
                .createdAt(Instant.now())
                .build();

        eventRepository.save(event);

        assertFalse(eventRepository.findByGroupId("groupX").isEmpty());
        assertFalse(eventRepository.findByCreatorId("userX").isEmpty());
        assertFalse(eventRepository.findByEventDateBetween(
                Instant.now().minusSeconds(60), Instant.now().plusSeconds(60)
        ).isEmpty());
    }
}