package com.alkemy.java2.TPIntegrador.controller.integration;

import com.alkemy.java2.TPIntegrador.model.Group;
import com.alkemy.java2.TPIntegrador.repository.GroupRepository;
import com.alkemy.java2.TPIntegrador.DTOs.GroupDTO;
import com.alkemy.java2.TPIntegrador.DTOs.UserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
class GroupControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private static final String OWNER_ID = "owner123";

    @BeforeEach
    void cleanDb() {
        groupRepository.deleteAll();
    }

    private Group buildGroup(String name, String description) {
        return Group.builder()
                .name(name)
                .description(description)
                .ownerId(OWNER_ID)
                .memberIds(Collections.emptyList())
                .isPublic(true)
                .createdAt(Instant.now())
                .build();
    }

    @Test
    void shouldCreateAndGetGroup() throws Exception {
        Group group = buildGroup("Rockstars", "Grupo de música");
        Group saved = groupRepository.save(group);

        mockMvc.perform(get("/api/groups/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Rockstars"))
                .andExpect(jsonPath("$.ownerId").value(OWNER_ID));
    }

    @Test
    void shouldCreateGroupViaPost() throws Exception {
        GroupDTO payload = new GroupDTO();
        payload.setName("TestGroup");
        payload.setDescription("Desc");
        payload.setPublic(true);
        payload.setOwnerId(OWNER_ID);
        payload.setMemberIds(Collections.emptyList());

        mockMvc.perform(post("/api/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("TestGroup"))
                .andExpect(jsonPath("$.ownerId").value(OWNER_ID));
    }

    @Test
    void shouldAddMember() throws Exception {
        Group group = groupRepository.save(buildGroup("Fans", "Grupo de fans"));
        UserDTO userDto = new UserDTO();
        userDto.setId("123");
        userDto.setUsername("u");
        userDto.setEmail("m@il");
        userDto.setFullName("Full");
        userDto.setProfileImageUrl("img");
        userDto.setGroupIds(Collections.emptyList());

        mockMvc.perform(post("/api/groups/" + group.getId() + "/add-member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberIds[0]").value("123"));
    }

    @Test
    void shouldGetAllGroups() throws Exception {
        groupRepository.save(buildGroup("General", "Grupo general"));
        mockMvc.perform(get("/api/groups"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").exists());
    }

@Test
void shouldGetAllGroupsAsync() throws Exception {
    groupRepository.save(buildGroup("Async", "Grupo async"));

    var mvcResult = mockMvc.perform(get("/api/groups/async"))
            .andExpect(request().asyncStarted())
            .andReturn();

    mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].name").value("Async"));
}

    @Test
    void shouldProcessGroupsParallel() throws Exception {
        Group g1 = groupRepository.save(buildGroup("Uno", "Desc1"));
        Group g2 = groupRepository.save(buildGroup("Dos", "Desc2"));

        mockMvc.perform(post("/api/groups/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(g1.getId(), g2.getId()))))
                .andExpect(status().isOk())
                .andExpect(content().string("Procesamiento en paralelo iniciado para 2 grupos."));
    }
}