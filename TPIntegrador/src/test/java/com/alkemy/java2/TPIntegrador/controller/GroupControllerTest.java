package com.alkemy.java2.TPIntegrador.controller;

import com.alkemy.java2.TPIntegrador.DTOs.GroupDTO;
import com.alkemy.java2.TPIntegrador.DTOs.UserDTO;
import com.alkemy.java2.TPIntegrador.service.GroupService;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GroupControllerTest {

    @Mock
    private GroupService groupService;

    @InjectMocks
    private GroupController controller;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void createGroup_HappyPath() {
        // Arrange
        GroupDTO dto = new GroupDTO("name", "desc", true, "owner", new ArrayList<>());
        GroupDTO saved = new GroupDTO("name", "desc", true, "owner", new ArrayList<>());
        when(groupService.createGroup(dto)).thenReturn(saved);

        // Act
        ResponseEntity<GroupDTO> response = controller.createGroup(dto);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(saved, response.getBody());
        verify(groupService).createGroup(dto);
    }

    @Test
    void getGroup_HappyPath() {
        // Arrange
        String id = "1";
        GroupDTO dto = new GroupDTO("name", "desc", true, "owner", new ArrayList<>());
        when(groupService.getGroupById(id)).thenReturn(dto);

        // Act
        ResponseEntity<GroupDTO> response = controller.getGroup(id);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dto, response.getBody());
        verify(groupService).getGroupById(id);
    }

    @Test
    void getGroup_NotFound() {
        // Arrange
        String id = "2";
        when(groupService.getGroupById(id)).thenReturn(null);

        // Act
        ResponseEntity<GroupDTO> response = controller.getGroup(id);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(groupService).getGroupById(id);
    }

    @Test
    void addMember_HappyPath() {
        // Arrange
        String id = "1";
        UserDTO user = new UserDTO();
        GroupDTO updated = new GroupDTO("name", "desc", true, "owner", List.of("u1"));
        when(groupService.addMemberToGroup(id, user)).thenReturn(updated);

        // Act
        ResponseEntity<GroupDTO> response = controller.addMember(id, user);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(updated, response.getBody());
        verify(groupService).addMemberToGroup(id, user);
    }

    @Test
    void addMember_GroupNotFound() {
        // Arrange
        String id = "2";
        UserDTO user = new UserDTO();
        when(groupService.addMemberToGroup(id, user)).thenReturn(null);

        // Act
        ResponseEntity<GroupDTO> response = controller.addMember(id, user);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(groupService).addMemberToGroup(id, user);
    }

    @Test
    void getAllGroups_HappyPath() {
        // Arrange
        List<GroupDTO> list = List.of(new GroupDTO("n", "d", true, "o", new ArrayList<>()));
        when(groupService.getAllGroups()).thenReturn(list);

        // Act
        ResponseEntity<List<GroupDTO>> response = controller.getAllGroups();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(list, response.getBody());
        assertFalse(response.getBody().isEmpty());
        verify(groupService).getAllGroups();
    }

    @Test
    void getAllGroups_EmptyList() {
        // Arrange
        when(groupService.getAllGroups()).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<GroupDTO>> response = controller.getAllGroups();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isEmpty());
        verify(groupService).getAllGroups();
    }

    @Test
    void getAllGroupsAsync_HappyPath() throws Exception {
        // Arrange
        List<GroupDTO> list = List.of(new GroupDTO("n", "d", true, "o", new ArrayList<>()));
        CompletableFuture<List<GroupDTO>> future = CompletableFuture.completedFuture(list);
        when(groupService.getAllGroupsAsync()).thenReturn(future);

        // Act
        CompletableFuture<ResponseEntity<List<GroupDTO>>> resultFuture = controller.getAllGroupsAsync();
        ResponseEntity<List<GroupDTO>> response = resultFuture.get(1, TimeUnit.SECONDS);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(list, response.getBody());
        verify(groupService).getAllGroupsAsync();
    }

    @Test
    void processGroups_HappyPath() {
        // Arrange
        List<String> ids = List.of("1", "2");
        doNothing().when(groupService).processMultipleGroups(ids);

        // Act
        ResponseEntity<String> response = controller.processGroups(ids);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("2"));
        verify(groupService).processMultipleGroups(ids);
    }

    @Test
    void processGroups_EmptyList() {
        // Arrange
        List<String> ids = Collections.emptyList();
        doNothing().when(groupService).processMultipleGroups(ids);

        // Act
        ResponseEntity<String> response = controller.processGroups(ids);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("0"));
        verify(groupService).processMultipleGroups(ids);
    }
}