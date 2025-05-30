package com.alkemy.java2.TPIntegrador.repository;


import com.alkemy.java2.TPIntegrador.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
}