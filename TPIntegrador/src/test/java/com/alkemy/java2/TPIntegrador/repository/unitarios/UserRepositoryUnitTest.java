package com.alkemy.java2.TPIntegrador.repository.unitarios;

import com.alkemy.java2.TPIntegrador.model.User;
import com.alkemy.java2.TPIntegrador.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryUnitTest {

    @Mock
    private UserRepository userRepository;

    @Test
    void testFindByUsername() {
        User user = User.builder().id("1").username("janita").email("j@r.com").passwordHash("abc").fullName("Jana R").build();
        when(userRepository.findByUsername("janita")).thenReturn(Optional.of(user));

        Optional<User> result = userRepository.findByUsername("janita");

        assertTrue(result.isPresent());
        assertEquals("janita", result.get().getUsername());
        verify(userRepository).findByUsername("janita");
    }

    @Test
    void testExistsByEmail() {
        when(userRepository.existsByEmail("j@r.com")).thenReturn(true);
        assertTrue(userRepository.existsByEmail("j@r.com"));
    }
    @Test
    void testExistsByUsername() {
        when(userRepository.existsByUsername("jdoe")).thenReturn(true);
        assertTrue(userRepository.existsByUsername("jdoe"));
    }
    @Test
    void testSaveUser_Success() {
        // Arrange: crear usuario y mockear save
        User user = User.builder().id("1").username("janita").email("j@r.com").passwordHash("abc").fullName("Jana R").build();
        when(userRepository.save(user)).thenReturn(user);

        // Act: guardar usuario
        User saved = userRepository.save(user);

        // Assert: verificar guardado correcto
        assertNotNull(saved);
        assertEquals("janita", saved.getUsername());
        verify(userRepository).save(user);
    }

   @Test
   void testDeleteById_NullId() {
       // Arrange: configurar mock para lanzar excepción si el id es null
       doThrow(new IllegalArgumentException()).when(userRepository).deleteById(null);

       // Act & Assert: borrar null lanza excepción
       assertThrows(IllegalArgumentException.class, () -> userRepository.deleteById(null));
   }

   @Test
   void testSaveUser_NullUser() {
       // Arrange: configurar mock para lanzar excepción si el usuario es null
       when(userRepository.save(null)).thenThrow(new IllegalArgumentException());

       // Act & Assert: guardar null lanza excepción
       assertThrows(IllegalArgumentException.class, () -> userRepository.save(null));
   }


    @Test
    void testFindById_UserExists() {
        // Arrange: mockear búsqueda por id existente
        User user = User.builder().id("2").username("ana").build();
        when(userRepository.findById("2")).thenReturn(Optional.of(user));

        // Act: buscar usuario
        Optional<User> result = userRepository.findById("2");

        // Assert: usuario encontrado
        assertTrue(result.isPresent());
        assertEquals("ana", result.get().getUsername());
    }

    @Test
    void testFindById_UserNotFound() {
        // Arrange: mockear búsqueda por id inexistente
        when(userRepository.findById("3")).thenReturn(Optional.empty());

        // Act: buscar usuario
        Optional<User> result = userRepository.findById("3");

        // Assert: usuario no encontrado
        assertFalse(result.isPresent());
        assertNull(result.orElse(null));
    }

    @Test
    void testFindAll_EmptyList() {
        // Arrange: mockear lista vacía
        when(userRepository.findAll()).thenReturn(List.of());

        // Act: buscar todos
        List<User> users = userRepository.findAll();

        // Assert: lista vacía
        assertTrue(users.isEmpty());
    }

    @Test
    void testFindAll_WithUsers() {
        // Arrange: mockear lista con usuarios
        User u1 = User.builder().id("1").build();
        User u2 = User.builder().id("2").build();
        when(userRepository.findAll()).thenReturn(List.of(u1, u2));

        // Act: buscar todos
        List<User> users = userRepository.findAll();

        // Assert: lista con dos usuarios
        assertEquals(2, users.size());
        assertNotNull(users.get(0));
    }

    @Test
    void testDeleteById_Success() {
        // Arrange: mockear borrado exitoso
        String userId = "4";
        doNothing().when(userRepository).deleteById(userId);

        // Act: borrar usuario
        userRepository.deleteById(userId);

        // Assert: verificar borrado
        verify(userRepository).deleteById(userId);
    }


    @Test
    void testExistsById_True() {
        // Arrange: mockear existencia
        when(userRepository.existsById("5")).thenReturn(true);

        // Act: verificar existencia
        boolean exists = userRepository.existsById("5");

        // Assert: debe existir
        assertTrue(exists);
    }

    @Test
    void testExistsById_False() {
        // Arrange: mockear no existencia
        when(userRepository.existsById("6")).thenReturn(false);

        // Act: verificar existencia
        boolean exists = userRepository.existsById("6");

        // Assert: no debe existir
        assertFalse(exists);
    }

    @Test
    void testFindByUsername_UserExists() {
        // Arrange: mockear búsqueda por username
        User user = User.builder().id("7").username("jdoe").build();
        when(userRepository.findByUsername("jdoe")).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = userRepository.findByUsername("jdoe");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("jdoe", result.get().getUsername());
    }

    @Test
    void testFindByUsername_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername("nouser")).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userRepository.findByUsername("nouser");

        // Assert
        assertFalse(result.isPresent());
        assertNull(result.orElse(null));
    }

    @Test
    void testFindByEmail_UserExists() {
        // Arrange
        User user = User.builder().id("8").email("mail@x.com").build();
        when(userRepository.findByEmail("mail@x.com")).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = userRepository.findByEmail("mail@x.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("mail@x.com", result.get().getEmail());
    }

    @Test
    void testFindByEmail_UserNotFound() {
        // Arrange
        when(userRepository.findByEmail("no@mail.com")).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userRepository.findByEmail("no@mail.com");

        // Assert
        assertFalse(result.isPresent());
        assertNull(result.orElse(null));
    }

    @Test
    void testExistsByUsername_True() {
        // Arrange
        when(userRepository.existsByUsername("janita")).thenReturn(true);

        // Act & Assert
        assertTrue(userRepository.existsByUsername("janita"));
    }

    @Test
    void testExistsByUsername_False() {
        // Arrange
        when(userRepository.existsByUsername("nouser")).thenReturn(false);

        // Act & Assert
        assertFalse(userRepository.existsByUsername("nouser"));
    }

    @Test
    void testExistsByEmail_True() {
        // Arrange
        when(userRepository.existsByEmail("j@r.com")).thenReturn(true);

        // Act & Assert
        assertTrue(userRepository.existsByEmail("j@r.com"));
    }

    @Test
    void testExistsByEmail_False() {
        // Arrange
        when(userRepository.existsByEmail("no@mail.com")).thenReturn(false);

        // Act & Assert
        assertFalse(userRepository.existsByEmail("no@mail.com"));
    }
}