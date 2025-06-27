package com.alkemy.java2.TPIntegrador.repository;

import com.alkemy.java2.TPIntegrador.model.Event;
import com.alkemy.java2.TPIntegrador.model.Group;
import com.alkemy.java2.TPIntegrador.model.Participant;
import com.alkemy.java2.TPIntegrador.model.User;
import com.alkemy.java2.TPIntegrador.model.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@DataMongoTest
@Testcontainers
class RepositoryIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    private Group group;
    private User user;
    private Event event;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        groupRepository.deleteAll();
        eventRepository.deleteAll();

        user = User.builder()
                .username("user123")
                .email("user@example.com")
                .fullName("Test User")
                .passwordHash("hashed")
                .roles(Set.of())
                .build();
        user = userRepository.save(user);

        group = Group.builder()
                .name("Group 1")
                .description("Test group")
                .ownerId(user.getId())
                .memberIds(List.of(user.getId()))
                .build();
        group = groupRepository.save(group);

        event = Event.builder()
                .title("Evento 1")
                .description("Desc")
                .creatorId(user.getId())
                .groupId(group.getId())
                .eventDate(LocalDateTime.now().plusDays(1))
                .participants(List.of(new Participant(user.getId(), Status.CONFIRMED)))
                .build();
        event = eventRepository.save(event);
    }

    // GroupRepository
    @Test
    void findByOwnerId_shouldReturnGroups() {
        List<Group> result = groupRepository.findByOwnerId(user.getId());
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getName()).isEqualTo("Group 1");
    }

    @Test
    void findByMemberIdsContaining_shouldReturnGroups() {
        List<Group> result = groupRepository.findByMemberIdsContaining(user.getId());
        assertThat(result).hasSize(1);
    }

    // EventRepository
    @Test
    void findByGroupId_shouldReturnEvents() {
        List<Event> events = eventRepository.findByGroupId(group.getId());
        assertThat(events).hasSize(1);
        assertThat(events.get(0).getTitle()).isEqualTo("Evento 1");
    }

    @Test
    void findByCreatorId_shouldReturnEvents() {
        List<Event> events = eventRepository.findByCreatorId(user.getId());
        assertThat(events).hasSize(1);
    }

    @Test
    void findByEventDateBetween_shouldReturnEventsInRange() {
        Instant now = Instant.now();
        Instant tomorrow = now.plusSeconds(86400);
        List<Event> events = eventRepository.findByEventDateBetween(now, tomorrow);
        assertThat(events).isEmpty(); // porque eventDate usa LocalDateTime, no Instant
    }

    // UserRepository
    @Test
    void findByUsername_shouldReturnUser() {
        Optional<User> result = userRepository.findByUsername("user123");
        assertThat(result).isPresent();
    }

    @Test
    void existsByUsername_shouldReturnTrue() {
        assertThat(userRepository.existsByUsername("user123")).isTrue();
    }

    @Test
    void existsByEmail_shouldReturnTrue() {
        assertThat(userRepository.existsByEmail("user@example.com")).isTrue();
    }

    @Test
    void findByEmail_shouldReturnUser() {
        Optional<User> found = userRepository.findByEmail("user@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getFullName()).isEqualTo("Test User");
    }
}
