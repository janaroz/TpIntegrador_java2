package com.alkemy.java2.TPIntegrador.repository.integracion;

import com.alkemy.java2.TPIntegrador.MongoDB.BaseMongoIntegrationTest;
import com.alkemy.java2.TPIntegrador.model.Event;
import com.alkemy.java2.TPIntegrador.model.Participant;
import com.alkemy.java2.TPIntegrador.model.enums.Status;
import com.alkemy.java2.TPIntegrador.repository.EventRepository;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


import java.time.Instant;
import java.time.LocalDateTime;
import org.bson.Document;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class EventRepositoryIntegrationTest extends BaseMongoIntegrationTest {

    @Autowired
    private EventRepository eventRepository;
    @BeforeAll
    static void initReplicaSet() {
        // No hacer mongoDBContainer.start() si usás @Container arriba, lo maneja Testcontainers.

        try (MongoClient mongoClient = MongoClients.create(mongoDBContainer.getReplicaSetUrl())) {
            Document isMasterResult = mongoClient.getDatabase("admin")
                    .runCommand(new Document("isMaster", 1));
            if (!isMasterResult.containsKey("setName")) {
                mongoClient.getDatabase("admin")
                        .runCommand(new Document("replSetInitiate", new Document()));

                // Esperar hasta que el replica set esté iniciado
                int retries = 10;
                boolean initiated = false;
                while (retries > 0 && !initiated) {
                    Thread.sleep(1000); // 1 segundo
                    Document status = mongoClient.getDatabase("admin")
                            .runCommand(new Document("replSetGetStatus", new Document()));
                    if ("PRIMARY".equals(((Document)status.get("myState")).toString()) || status.get("ok").equals(1.0)) {
                        initiated = true;
                    }
                    retries--;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void shouldFindByGroupId() {
        Event event = Event.builder()
                .title("Fiesta")
                .groupId("groupTest")
                .eventDate(LocalDateTime.now())
                .build();
        eventRepository.save(event);

        assertThat(eventRepository.findByGroupId("groupTest")).hasSize(1);
    }
    @Test
    void sanityCheckMongoContainer() {
        assertThat(mongoDBContainer.isRunning()).isTrue();
        System.out.println("Mongo URI: " + mongoDBContainer.getReplicaSetUrl());
    }
    @Test
    void shouldFindByCreatorId() {
        Event event = Event.builder()
                .title("Charla")
                .creatorId("creator1")
                .eventDate(LocalDateTime.now())
                .build();
        eventRepository.save(event);

        assertThat(eventRepository.findByCreatorId("creator1")).hasSize(1);
    }
    @BeforeEach
    void cleanUp() {
        eventRepository.deleteAll();
    }
    @Test
    void shouldNotFindEventOutsideDateRange() {
        Event event = Event.builder()
                .title("Antiguo")
                .eventDate(LocalDateTime.of(2000, 1, 1, 12, 0))
                .createdAt(Instant.now())
                .build();
        eventRepository.save(event);

        Instant start = Instant.now().minusSeconds(60);
        Instant end = Instant.now().plusSeconds(60);

        List<Event> result = eventRepository.findByEventDateBetween(start, end);
        assertThat(result).isEmpty();
    }

    @Test
    void shouldSupportParticipants() {
        Participant participant = new Participant("userIdX", Status.CONFIRMED);
        Event event = Event.builder()
                .title("EventoConParticipante")
                .participants(List.of(participant))
                .eventDate(LocalDateTime.now())
                .build();
        eventRepository.save(event);

        List<Event> result = eventRepository.findByGroupId(event.getGroupId());
        assertThat(result).isNotEmpty(); // Solo validamos que guarda correctamente
    }
}
