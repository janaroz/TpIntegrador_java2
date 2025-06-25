package com.alkemy.java2.TPIntegrador.controller;

import com.alkemy.java2.TPIntegrador.DTOs.UserDTO;
import com.alkemy.java2.TPIntegrador.service.UserService;
import org.junit.jupiter.api.*;
import org.mockito.*;
import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController controller;

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
    void create_HappyPath() {
        // Arrange
        UserDTO dto = new UserDTO();
        UserDTO saved = new UserDTO();
        when(userService.createUser(dto)).thenReturn(saved);

        // Act
        UserDTO result = controller.create(dto);

        // Assert
        assertNotNull(result);
        verify(userService).createUser(dto);
    }

    @Test
    void get_HappyPath() {
        // Arrange
        String id = "1";
        UserDTO dto = new UserDTO();
        when(userService.getUserById(id)).thenReturn(dto);

        // Act
        UserDTO result = controller.get(id);

        // Assert
        assertNotNull(result);
        verify(userService).getUserById(id);
    }

    @Test
    void get_NotFound() {
        // Arrange
        String id = "2";
        when(userService.getUserById(id)).thenReturn(null);

        // Act
        UserDTO result = controller.get(id);

        // Assert
        assertNull(result);
        verify(userService).getUserById(id);
    }

    @Test
    void all_HappyPath() {
        // Arrange
        List<UserDTO> list = List.of(new UserDTO());
        when(userService.getAllUsers()).thenReturn(list);

        // Act
        List<UserDTO> result = controller.all();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userService).getAllUsers();
    }

    @Test
    void all_EmptyList() {
        // Arrange
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        // Act
        List<UserDTO> result = controller.all();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userService).getAllUsers();
    }

    @Test
    void update_HappyPath() {
        // Arrange
        UserDTO dto = new UserDTO();
        UserDTO updated = new UserDTO();
        when(userService.updateUser(dto)).thenReturn(updated);

        // Act
        UserDTO result = controller.update(dto);

        // Assert
        assertNotNull(result);
        verify(userService).updateUser(dto);
    }

    @Test
    void delete_HappyPath() {
        // Arrange
        String id = "1";
        doNothing().when(userService).deleteUser(id);

        // Act
        controller.delete(id);

        // Assert
        verify(userService).deleteUser(id);
    }

    @Test
    void getAllUsersAsync_HappyPath() throws Exception {
        // Arrange
        List<UserDTO> list = List.of(new UserDTO());
        CompletableFuture<List<UserDTO>> future = CompletableFuture.completedFuture(list);
        when(userService.getAllUsersAsync()).thenReturn(future);

        // Act
        CompletableFuture<List<UserDTO>> resultFuture = controller.getAllUsersAsync();
        List<UserDTO> result = resultFuture.get(1, TimeUnit.SECONDS);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userService).getAllUsersAsync();
    }

    @Test
    void processUsers_HappyPath() {
        // Arrange
        List<String> ids = List.of("1", "2");
        doNothing().when(userService).processUsersByIdList(ids);

        // Act
        String result = controller.processUsers(ids);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("2"));
        verify(userService).processUsersByIdList(ids);
    }

    @Test
    void processUsers_EmptyList() {
        // Arrange
        List<String> ids = Collections.emptyList();
        doNothing().when(userService).processUsersByIdList(ids);

        // Act
        String result = controller.processUsers(ids);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("0"));
        verify(userService).processUsersByIdList(ids);
    }
}