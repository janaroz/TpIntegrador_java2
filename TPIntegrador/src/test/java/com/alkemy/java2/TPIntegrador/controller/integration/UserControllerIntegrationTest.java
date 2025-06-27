package com.alkemy.java2.TPIntegrador.controller.integration;
import com.alkemy.java2.TPIntegrador.DTOs.UserDTO;
import com.alkemy.java2.TPIntegrador.model.User;
import com.alkemy.java2.TPIntegrador.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser(username = "admin", roles = {"ADMIN"})
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    void shouldCreateAndGetUser() throws Exception {
        UserDTO dto = new UserDTO();
        dto.setUsername("janita");
        dto.setEmail("janita@mail.com");
        dto.setFullName("Janita Tester");
        dto.setProfileImageUrl("http://img.com/janita");
        dto.setGroupIds(List.of("g1", "g2"));

        String json = objectMapper.writeValueAsString(dto);

        // Crear
        String responseJson = mockMvc.perform(post("/api/users")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("janita"))
                .andReturn().getResponse().getContentAsString();

        UserDTO created = objectMapper.readValue(responseJson, UserDTO.class);

        // Obtener por ID
        mockMvc.perform(get("/api/users/{id}", created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("janita"));
    }

    @Test
    void shouldGetAllUsers() throws Exception {
        userRepository.saveAll(List.of(
                new UserDTO(null, "ana", "ana@mail.com", "Ana User", "img", List.of("g1")),
                new UserDTO(null, "luis", "luis@mail.com", "Luis User", "img2", List.of("g2"))
        ).stream().map(dto -> {
            var user = new com.alkemy.java2.TPIntegrador.model.User();
            user.setUsername(dto.getUsername());
            user.setEmail(dto.getEmail());
            user.setFullName(dto.getFullName());
            user.setProfileImageUrl(dto.getProfileImageUrl());
            user.setGroupIds(dto.getGroupIds());
            user.setPasswordHash("hash");
            user.setRoles(Set.of());
            return user;
        }).toList());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].username", anyOf(is("ana"), is("luis"))));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        var user = new com.alkemy.java2.TPIntegrador.model.User();
        user.setUsername("original");
        user.setEmail("original@mail.com");
        user.setFullName("Original User");
        user.setProfileImageUrl("img");
        user.setGroupIds(List.of("g1"));
        user.setPasswordHash("hash");
        user.setRoles(Set.of());
        user = userRepository.save(user);

        UserDTO updateDto = new UserDTO(user.getId(), "original", "updated@mail.com", "Updated User", "newimg", List.of("g1"));
        String json = objectMapper.writeValueAsString(updateDto);

        mockMvc.perform(put("/api/users/")
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@mail.com"))
                .andExpect(jsonPath("$.fullName").value("Updated User"));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        var user = new com.alkemy.java2.TPIntegrador.model.User();
        user.setUsername("todelete");
        user.setEmail("del@mail.com");
        user.setFullName("Del User");
        user.setProfileImageUrl("img");
        user.setGroupIds(List.of("g1"));
        user.setPasswordHash("hash");
        user.setRoles(Set.of());
        user = userRepository.save(user);

        mockMvc.perform(delete("/api/users/{id}", user.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/users/{id}", user.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void shouldGetUsersAsync() throws Exception {
        var user = new User();
        user.setUsername("async");
        user.setEmail("async@mail.com");
        user.setFullName("Async User");
        user.setProfileImageUrl("img");
        user.setGroupIds(List.of("g1"));
        user.setPasswordHash("hash");
        user.setRoles(Set.of());
        userRepository.save(user);

        var mvcResult = mockMvc.perform(get("/api/users/async"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(content().contentType(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(jsonPath("$[0].username").value("async"));
    }

    @Test
    void shouldProcessUsersParallel() throws Exception {
        mockMvc.perform(post("/api/users/process")
                        .contentType("application/json")
                        .content("[\"123\", \"456\"]"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Procesamiento paralelo iniciado para 2 usuarios.")));
    }
}