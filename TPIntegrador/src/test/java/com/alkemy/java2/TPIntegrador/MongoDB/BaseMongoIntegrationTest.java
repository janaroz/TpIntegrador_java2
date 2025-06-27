package com.alkemy.java2.TPIntegrador.MongoDB;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.Document;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@DataMongoTest
public abstract class BaseMongoIntegrationTest {

    @Container
    public static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0")
            .withReuse(true)
            .withCommand("--replSet rs0")
            .waitingFor(Wait.forLogMessage(".*Waiting for connections.*", 1));

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeAll
    static void initReplicaSet() {
        try (MongoClient mongoClient = MongoClients.create(mongoDBContainer.getReplicaSetUrl())) {
            Document isMaster = mongoClient.getDatabase("admin")
                    .runCommand(new Document("isMaster", 1));
            if (!isMaster.containsKey("setName")) {
                mongoClient.getDatabase("admin")
                        .runCommand(new Document("replSetInitiate", new Document()));
                // Esperamos unos segundos para que el replica set se estabilice
                Thread.sleep(3000);
            }
        } catch (Exception e) {
            throw new RuntimeException("ReplicaSet init failed", e);
        }
    }
}
