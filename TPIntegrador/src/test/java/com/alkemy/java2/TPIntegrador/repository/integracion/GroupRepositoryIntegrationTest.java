package com.alkemy.java2.TPIntegrador.repository.integracion;
import com.alkemy.java2.TPIntegrador.MongoDB.BaseMongoIntegrationTest;
import com.alkemy.java2.TPIntegrador.model.Group;
import com.alkemy.java2.TPIntegrador.repository.GroupRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
@SpringBootTest
@Testcontainers
class GroupRepositoryIntegrationTest extends BaseMongoIntegrationTest {

    @Autowired
    private GroupRepository groupRepository;

    @Test
    void shouldSaveAndFindByOwnerAndMembers() {
        Group group = Group.builder()
                .name("Grupo1")
                .ownerId("owner123")
                .memberIds(List.of("member1", "member2"))
                .build();

        groupRepository.save(group);

        List<Group> byOwner = groupRepository.findByOwnerId("owner123");
        List<Group> byMember = groupRepository.findByMemberIdsContaining("member1");

        assertFalse(byOwner.isEmpty());
        assertFalse(byMember.isEmpty());
    }
}