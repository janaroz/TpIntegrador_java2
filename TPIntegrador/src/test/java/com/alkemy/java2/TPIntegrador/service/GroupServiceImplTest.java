package com.alkemy.java2.TPIntegrador.service;

import com.alkemy.java2.TPIntegrador.DTOs.GroupDTO;
import com.alkemy.java2.TPIntegrador.DTOs.UserDTO;
import com.alkemy.java2.TPIntegrador.mappers.GenericMapper;
import com.alkemy.java2.TPIntegrador.model.Group;
import com.alkemy.java2.TPIntegrador.repository.GroupRepository;
import com.alkemy.java2.TPIntegrador.service.impl.GroupServiceImpl;
import org.junit.jupiter.api.*;
import org.mockito.*;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GroupServiceImplTest {

    @Mock private GroupRepository groupRepository;
    @Mock private GenericMapper genericMapper;
    @InjectMocks private GroupServiceImpl groupService;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        groupService = new GroupServiceImpl(groupRepository, genericMapper);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        groupService.shutdown();
    }

    @Test
    void createGroup_HappyPath() {
        // Arrange
        GroupDTO dto = new GroupDTO("TestGroup", "desc", true, "owner1", new ArrayList<>());
        Group group = Group.builder()
                .id("1")
                .name("TestGroup")
                .description("desc")
                .isPublic(true)
                .ownerId("owner1")
                .memberIds(new ArrayList<>())
                .build();
        GroupDTO resultDto = new GroupDTO("TestGroup", "desc", true, "owner1", new ArrayList<>());

        when(genericMapper.toEntity(dto, Group.class)).thenReturn(group);
        when(groupRepository.save(group)).thenReturn(group);
        when(genericMapper.toDTO(group, GroupDTO.class)).thenReturn(resultDto);

        // Act
        GroupDTO result = groupService.createGroup(dto);

        // Assert
        assertNotNull(result);
        assertEquals("TestGroup", result.getName());
        verify(groupRepository).save(group);
    }

    @Test
    void getGroupById_Found() {
        // Arrange
        String id = "1";
        Group group = Group.builder()
                .id(id)
                .name("TestGroup")
                .description("desc")
                .isPublic(true)
                .ownerId("owner1")
                .memberIds(new ArrayList<>())
                .build();
        GroupDTO dto = new GroupDTO("TestGroup", "desc", true, "owner1", new ArrayList<>());
        when(groupRepository.findById(id)).thenReturn(Optional.of(group));
        when(genericMapper.toDTO(group, GroupDTO.class)).thenReturn(dto);

        // Act
        GroupDTO result = groupService.getGroupById(id);

        // Assert
        assertNotNull(result);
        assertEquals("TestGroup", result.getName());
        verify(groupRepository).findById(id);
    }

    @Test
    void getGroupById_NotFound() {
        // Arrange
        String id = "2";
        when(groupRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        GroupDTO result = groupService.getGroupById(id);

        // Assert
        assertNull(result);
    }

    @Test
    void getAllGroups_ReturnsList() {
        // Arrange
        Group group = Group.builder()
                .id("1")
                .name("TestGroup")
                .description("desc")
                .isPublic(true)
                .ownerId("owner1")
                .memberIds(new ArrayList<>())
                .build();
        GroupDTO dto = new GroupDTO("TestGroup", "desc", true, "owner1", new ArrayList<>());
        when(groupRepository.findAll()).thenReturn(List.of(group));
        when(genericMapper.toDTO(group, GroupDTO.class)).thenReturn(dto);

        // Act
        List<GroupDTO> result = groupService.getAllGroups();

        // Assert
        assertEquals(1, result.size());
        assertEquals("TestGroup", result.get(0).getName());
        verify(groupRepository).findAll();
    }

    @Test
    void getAllGroups_EmptyList() {
        // Arrange
        when(groupRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<GroupDTO> result = groupService.getAllGroups();

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllGroupsAsync_HappyPath() throws Exception {
        // Arrange
        Group group = Group.builder()
                .id("1")
                .name("TestGroup")
                .description("desc")
                .isPublic(true)
                .ownerId("owner1")
                .memberIds(new ArrayList<>())
                .build();
        GroupDTO dto = new GroupDTO("TestGroup", "desc", true, "owner1", new ArrayList<>());
        when(groupRepository.findAll()).thenReturn(List.of(group));
        when(genericMapper.toDTO(group, GroupDTO.class)).thenReturn(dto);

        // Act
        CompletableFuture<List<GroupDTO>> future = groupService.getAllGroupsAsync();
        List<GroupDTO> result = future.get(2, TimeUnit.SECONDS);

        // Assert
        assertEquals(1, result.size());
        assertEquals("TestGroup", result.get(0).getName());
    }

    @Test
    void addMemberToGroup_Found() {
        // Arrange
        String groupId = "1";
        UserDTO userDTO = new UserDTO();
        userDTO.setId("u1");
        Group group = Group.builder()
                .id(groupId)
                .name("TestGroup")
                .description("desc")
                .isPublic(true)
                .ownerId("owner1")
                .memberIds(new ArrayList<>())
                .build();
        GroupDTO dto = new GroupDTO("TestGroup", "desc", true, "owner1", new ArrayList<>(List.of("u1")));

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(groupRepository.save(any(Group.class))).thenReturn(group);
        when(genericMapper.toDTO(group, GroupDTO.class)).thenReturn(dto);

        // Act
        GroupDTO result = groupService.addMemberToGroup(groupId, userDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getMemberIds().size());
        assertEquals("u1", result.getMemberIds().get(0));
    }

    @Test
    void addMemberToGroup_NotFound() {
        // Arrange
        String groupId = "2";
        UserDTO userDTO = new UserDTO();
        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        // Act
        GroupDTO result = groupService.addMemberToGroup(groupId, userDTO);

        // Assert
        assertNull(result);
    }

    @Test
    void processMultipleGroups_HandlesNotFound() throws InterruptedException {
        // Arrange
        String id1 = "1";
        String id2 = "2";
        Group group = Group.builder()
                .id(id1)
                .name("Group1")
                .description("desc")
                .isPublic(true)
                .ownerId("owner1")
                .memberIds(new ArrayList<>())
                .build();
        GroupDTO dto = new GroupDTO("Group1", "desc", true, "owner1", new ArrayList<>());
        when(groupRepository.findById(id1)).thenReturn(Optional.of(group));
        when(genericMapper.toDTO(group, GroupDTO.class)).thenReturn(dto);
        when(groupRepository.findById(id2)).thenReturn(Optional.empty());

        // Act
        groupService.processMultipleGroups(List.of(id1, id2));
        Thread.sleep(500); // Esperar a que los hilos terminen

        // Assert
        verify(groupRepository, atLeastOnce()).findById(id1);
        verify(groupRepository, atLeastOnce()).findById(id2);
    }
}