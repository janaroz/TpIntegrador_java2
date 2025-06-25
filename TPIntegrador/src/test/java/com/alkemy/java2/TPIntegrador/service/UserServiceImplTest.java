package com.alkemy.java2.TPIntegrador.service;

import com.alkemy.java2.TPIntegrador.DTOs.UserDTO;
import com.alkemy.java2.TPIntegrador.exceptions.ResourceNotFoundException;
import com.alkemy.java2.TPIntegrador.mappers.GenericMapper;
import com.alkemy.java2.TPIntegrador.model.User;
import com.alkemy.java2.TPIntegrador.repository.UserRepository;
import com.alkemy.java2.TPIntegrador.service.impl.UserServiceImpl;
import org.junit.jupiter.api.*;
import org.mockito.*;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock private UserRepository repo;
    @Mock private GenericMapper mapper;
    @InjectMocks private UserServiceImpl service;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        service = new UserServiceImpl(repo, mapper);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        service.shutdownExecutor();
    }

    @Test
    void createUser_HappyPath() {
        // Arrange
        UserDTO dto = new UserDTO();
        dto.setUsername("user");
        User user = new User();
        user.setUsername("user");
        when(mapper.toEntity(dto, User.class)).thenReturn(user);
        when(repo.existsByUsername("user")).thenReturn(false);
        when(repo.save(user)).thenReturn(user);
        when(mapper.toDTO(user, UserDTO.class)).thenReturn(dto);

        // Act
        UserDTO result = service.createUser(dto);

        // Assert
        assertNotNull(result);
        assertEquals("user", result.getUsername());
        verify(repo).save(user);
    }

    @Test
    void createUser_UsernameExists_Throws() {
        // Arrange
        UserDTO dto = new UserDTO();
        dto.setUsername("user");
        User user = new User();
        user.setUsername("user");
        when(mapper.toEntity(dto, User.class)).thenReturn(user);
        when(repo.existsByUsername("user")).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> service.createUser(dto));
    }

    @Test
    void getUserById_Found() {
        // Arrange
        String id = "1";
        User user = new User();
        UserDTO dto = new UserDTO();
        when(repo.findById(id)).thenReturn(Optional.of(user));
        when(mapper.toDTO(user, UserDTO.class)).thenReturn(dto);

        // Act
        UserDTO result = service.getUserById(id);

        // Assert
        assertNotNull(result);
        verify(repo).findById(id);
    }

    @Test
    void getUserById_NotFound_Throws() {
        // Arrange
        String id = "2";
        when(repo.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> service.getUserById(id));
    }

    @Test
    void getAllUsers_ReturnsList() {
        // Arrange
        User user = new User();
        UserDTO dto = new UserDTO();
        when(repo.findAll()).thenReturn(List.of(user));
        when(mapper.toDTO(user, UserDTO.class)).thenReturn(dto);

        // Act
        List<UserDTO> result = service.getAllUsers();

        // Assert
        assertEquals(1, result.size());
        verify(repo).findAll();
    }

    @Test
    void getAllUsers_EmptyList() {
        // Arrange
        when(repo.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<UserDTO> result = service.getAllUsers();

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllUsersAsync_HappyPath() throws Exception {
        // Arrange
        User user = new User();
        UserDTO dto = new UserDTO();
        when(repo.findAll()).thenReturn(List.of(user));
        when(mapper.toDTO(user, UserDTO.class)).thenReturn(dto);

        // Act
        CompletableFuture<List<UserDTO>> future = service.getAllUsersAsync();
        List<UserDTO> result = future.get(2, TimeUnit.SECONDS);

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    void updateUser_Found() {
        // Arrange
        UserDTO dto = new UserDTO();
        dto.setId("1");
        dto.setFullName("Nuevo");
        User user = new User();
        when(repo.findById("1")).thenReturn(Optional.of(user));
        when(repo.save(user)).thenReturn(user);
        when(mapper.toDTO(user, UserDTO.class)).thenReturn(dto);

        // Act
        UserDTO result = service.updateUser(dto);

        // Assert
        assertNotNull(result);
        assertEquals("Nuevo", result.getFullName());
    }

    @Test
    void updateUser_NotFound() {
        // Arrange
        UserDTO dto = new UserDTO();
        dto.setId("2");
        when(repo.findById("2")).thenReturn(Optional.empty());

        // Act
        UserDTO result = service.updateUser(dto);

        // Assert
        assertNull(result);
    }

    @Test
    void deleteUser_Deletes() {
        // Arrange
        String id = "1";
        doNothing().when(repo).deleteById(id);

        // Act
        service.deleteUser(id);

        // Assert
        verify(repo).deleteById(id);
    }

    @Test
    void processUsersByIdList_HandlesNotFound() throws InterruptedException {
        // Arrange
        String id1 = "1";
        String id2 = "2";
        User user = new User();
        UserDTO dto = new UserDTO();
        dto.setUsername("user");
        when(repo.findById(id1)).thenReturn(Optional.of(user));
        when(mapper.toDTO(user, UserDTO.class)).thenReturn(dto);
        when(repo.findById(id2)).thenReturn(Optional.empty());

        // Act
        service.processUsersByIdList(List.of(id1, id2));
        // Esperar un poco para que los hilos terminen
        Thread.sleep(500);

        // Assert
        verify(repo, atLeastOnce()).findById(id1);
        verify(repo, atLeastOnce()).findById(id2);
    }
}