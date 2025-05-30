package com.alkemy.java2.TPIntegrador.repository;

import com.alkemy.java2.TPIntegrador.model.Group;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface GroupRepository extends MongoRepository<Group, String> {
    List<Group> findByOwnerId(String ownerId);
    List<Group> findByMemberIdsContaining(String userId);
}