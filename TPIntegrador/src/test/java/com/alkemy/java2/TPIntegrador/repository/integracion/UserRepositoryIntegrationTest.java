package com.alkemy.java2.TPIntegrador.repository.integracion;

import com.alkemy.java2.TPIntegrador.MongoDB.BaseMongoIntegrationTest;
import com.alkemy.java2.TPIntegrador.model.User;
import com.alkemy.java2.TPIntegrador.repository.UserRepository;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class UserRepositoryIntegrationTest extends BaseMongoIntegrationTest {

    @Autowired
    private UserRepository userRepository;
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
    void shouldSaveAndFindByUsernameAndEmail() {
        User user = User.builder()
                .username("janita")
                .email("j@r.com")
                .passwordHash("hash")
                .fullName("Jana Nurit")
                .build();
        userRepository.save(user);

        assertThat(userRepository.findByUsername("janita")).isPresent();
        assertThat(userRepository.findByEmail("j@r.com")).isPresent();
    }

    @Test
    void shouldCheckExistenceCorrectly() {
        User user = User.builder()
                .username("existUser")
                .email("exist@u.com")
                .passwordHash("hash")
                .fullName("Existente")
                .build();
        userRepository.save(user);

        assertThat(userRepository.existsByUsername("existUser")).isTrue();
        assertThat(userRepository.existsByEmail("exist@u.com")).isTrue();
    }

    @Test
    void shouldReturnEmptyWhenUserNotExists() {
        Optional<User> result = userRepository.findByUsername("noExiste");
        assertThat(result).isNotPresent();

        assertThat(userRepository.existsByUsername("noExiste")).isFalse();
    }
}
