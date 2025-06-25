package com.alkemy.java2.TPIntegrador.repository.integracion;

import com.alkemy.java2.TPIntegrador.MongoDB.BaseMongoIntegrationTest;
import com.alkemy.java2.TPIntegrador.model.User;
import com.alkemy.java2.TPIntegrador.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertTrue;
@SpringBootTest
@Testcontainers
class UserRepositoryIntegrationTest extends BaseMongoIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndQueryByUsernameAndEmail() {
        User user = User.builder()
                .username("janita")
                .email("j@r.com")
                .passwordHash("hash")
                .fullName("Jana")
                .build();

        userRepository.save(user);

        assertTrue(userRepository.findByUsername("janita").isPresent());
        assertTrue(userRepository.findByEmail("j@r.com").isPresent());
        assertTrue(userRepository.existsByUsername("janita"));
        assertTrue(userRepository.existsByEmail("j@r.com"));
    }
}