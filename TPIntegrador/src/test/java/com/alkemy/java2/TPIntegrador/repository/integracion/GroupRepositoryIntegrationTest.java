package com.alkemy.java2.TPIntegrador.repository.integracion;
import com.alkemy.java2.TPIntegrador.MongoDB.BaseMongoIntegrationTest;
import com.alkemy.java2.TPIntegrador.model.Group;
import com.alkemy.java2.TPIntegrador.repository.GroupRepository;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.bson.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

class GroupRepositoryIntegrationTest extends BaseMongoIntegrationTest {

    @Autowired
    private GroupRepository groupRepository;
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
    void sanityCheckMongoContainer() {
        assertThat(mongoDBContainer.isRunning()).isTrue();
        System.out.println("Mongo URI: " + mongoDBContainer.getReplicaSetUrl());
    }
    @Test
    void shouldSaveAndFindByOwnerId() {
        Group group = Group.builder()
                .name("Grupo1")
                .ownerId("owner123")
                .memberIds(List.of("user1"))
                .build();
        groupRepository.save(group);

        List<Group> result = groupRepository.findByOwnerId("owner123");
        assertThat(result).hasSize(1);
    }

    @Test
    void shouldFindByMemberIdsContaining() {
        Group group = Group.builder()
                .name("Grupo2")
                .ownerId("owner456")
                .memberIds(List.of("memberX", "memberY"))
                .build();
        groupRepository.save(group);

        List<Group> result = groupRepository.findByMemberIdsContaining("memberY");
        assertThat(result).hasSize(1);
    }

    @Test
    void shouldNotFindGroupWhenMemberIdIsAbsent() {
        Group group = Group.builder()
                .name("Grupo3")
                .ownerId("owner789")
                .memberIds(List.of("member1", "member2"))
                .build();
        groupRepository.save(group);

        List<Group> result = groupRepository.findByMemberIdsContaining("unknownMember");
        assertThat(result).isEmpty();
    }
}