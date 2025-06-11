package com.alkemy.java2.TPIntegrador.repository;

import com.alkemy.java2.TPIntegrador.model.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;

public interface EventRepository extends MongoRepository<Event, String> {
    List<Event> findByGroupId(String groupId);
    List<Event> findByEventDateBetween(Instant start, Instant end);
}