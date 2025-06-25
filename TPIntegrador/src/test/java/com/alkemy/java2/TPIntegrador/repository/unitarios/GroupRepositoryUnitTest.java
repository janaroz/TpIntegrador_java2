package com.alkemy.java2.TPIntegrador.repository.unitarios;
import com.alkemy.java2.TPIntegrador.model.Group;
import com.alkemy.java2.TPIntegrador.repository.GroupRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupRepositoryUnitTest {

    @Mock
    private GroupRepository groupRepository;

    @Test
    void testFindByOwnerId1() {
        Group g = Group.builder().ownerId("owner123").name("Grupo A").build();
        when(groupRepository.findByOwnerId("owner123")).thenReturn(List.of(g));

        List<Group> result = groupRepository.findByOwnerId("owner123");

        assertEquals(1, result.size());
        assertEquals("Grupo A", result.get(0).getName());
    }
    @Test
    void testFindByOwnerId2() {
        Group group = Group.builder().ownerId("123").build();
        when(groupRepository.findByOwnerId("123")).thenReturn(List.of(group));

        List<Group> result = groupRepository.findByOwnerId("123");
        assertFalse(result.isEmpty());
    }

    @Test
    void testFindByMemberIdsContaining() {
        Group group = Group.builder().memberIds(List.of("456")).build();
        when(groupRepository.findByMemberIdsContaining("456")).thenReturn(List.of(group));

        List<Group> result = groupRepository.findByMemberIdsContaining("456");
        assertEquals(1, result.size());
    }
    @Test
    void testSaveGroup_Success() {
        // Arrange: crear grupo y mockear save
        Group group = Group.builder().id("1").name("Grupo1").ownerId("owner1").build();
        when(groupRepository.save(group)).thenReturn(group);

        // Act: guardar grupo
        Group saved = groupRepository.save(group);

        // Assert: verificar guardado correcto
        assertNotNull(saved);
        assertEquals("Grupo1", saved.getName());
        verify(groupRepository).save(group);
    }

    @Test
    void testSaveGroup_NullGroup() {
        // Arrange: mockear save(null) lanza excepción
        when(groupRepository.save(null)).thenThrow(new IllegalArgumentException());

        // Act & Assert: guardar null lanza excepción
        assertThrows(IllegalArgumentException.class, () -> groupRepository.save(null));
    }

    @Test
    void testFindById_GroupExists() {
        // Arrange: mockear búsqueda por id existente
        Group group = Group.builder().id("2").name("Grupo2").build();
        when(groupRepository.findById("2")).thenReturn(Optional.of(group));

        // Act: buscar grupo
        Optional<Group> result = groupRepository.findById("2");

        // Assert: grupo encontrado
        assertTrue(result.isPresent());
        assertEquals("Grupo2", result.get().getName());
    }

    @Test
    void testFindById_GroupNotFound() {
        // Arrange: mockear búsqueda por id inexistente
        when(groupRepository.findById("3")).thenReturn(Optional.empty());

        // Act: buscar grupo
        Optional<Group> result = groupRepository.findById("3");

        // Assert: grupo no encontrado
        assertFalse(result.isPresent());
        assertNull(result.orElse(null));
    }

    @Test
    void testFindAll_EmptyList() {
        // Arrange: mockear lista vacía
        when(groupRepository.findAll()).thenReturn(List.of());

        // Act: buscar todos
        List<Group> groups = groupRepository.findAll();

        // Assert: lista vacía
        assertTrue(groups.isEmpty());
    }

    @Test
    void testFindAll_WithGroups() {
        // Arrange: mockear lista con grupos
        Group g1 = Group.builder().id("1").build();
        Group g2 = Group.builder().id("2").build();
        when(groupRepository.findAll()).thenReturn(List.of(g1, g2));

        // Act: buscar todos
        List<Group> groups = groupRepository.findAll();

        // Assert: lista con dos grupos
        assertEquals(2, groups.size());
        assertNotNull(groups.get(0));
    }

    @Test
    void testDeleteById_Success() {
        // Arrange: mockear borrado exitoso
        String groupId = "4";
        doNothing().when(groupRepository).deleteById(groupId);

        // Act: borrar grupo
        groupRepository.deleteById(groupId);

        // Assert: verificar borrado
        verify(groupRepository).deleteById(groupId);
    }

    @Test
    void testDeleteById_NullId() {
        // Arrange: mockear deleteById(null) lanza excepción
        doThrow(new IllegalArgumentException()).when(groupRepository).deleteById(null);

        // Act & Assert: borrar null lanza excepción
        assertThrows(IllegalArgumentException.class, () -> groupRepository.deleteById(null));
    }

    @Test
    void testExistsById_True() {
        // Arrange: mockear existencia
        when(groupRepository.existsById("5")).thenReturn(true);

        // Act: verificar existencia
        boolean exists = groupRepository.existsById("5");

        // Assert: debe existir
        assertTrue(exists);
    }

    @Test
    void testExistsById_False() {
        // Arrange: mockear no existencia
        when(groupRepository.existsById("6")).thenReturn(false);

        // Act: verificar existencia
        boolean exists = groupRepository.existsById("6");

        // Assert: no debe existir
        assertFalse(exists);
    }

    @Test
    void testFindByOwnerId_WithGroups() {
        // Arrange: mockear búsqueda por ownerId
        Group group = Group.builder().ownerId("owner123").name("Grupo A").build();
        when(groupRepository.findByOwnerId("owner123")).thenReturn(List.of(group));

        // Act: buscar por ownerId
        List<Group> result = groupRepository.findByOwnerId("owner123");

        // Assert: grupo encontrado
        assertEquals(1, result.size());
        assertEquals("Grupo A", result.get(0).getName());
    }

    @Test
    void testFindByOwnerId_Empty() {
        // Arrange: ownerId sin grupos
        when(groupRepository.findByOwnerId("noOwner")).thenReturn(List.of());

        // Act: buscar por ownerId
        List<Group> result = groupRepository.findByOwnerId("noOwner");

        // Assert: lista vacía
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByMemberIdsContaining_WithGroups() {
        // Arrange: mockear búsqueda por miembro
        Group group = Group.builder().memberIds(List.of("user1")).build();
        when(groupRepository.findByMemberIdsContaining("user1")).thenReturn(List.of(group));

        // Act: buscar por miembro
        List<Group> result = groupRepository.findByMemberIdsContaining("user1");

        // Assert: grupo encontrado
        assertEquals(1, result.size());
        assertTrue(result.get(0).getMemberIds().contains("user1"));
    }

    @Test
    void testFindByMemberIdsContaining_Empty() {
        // Arrange: miembro sin grupos
        when(groupRepository.findByMemberIdsContaining("noUser")).thenReturn(List.of());

        // Act: buscar por miembro
        List<Group> result = groupRepository.findByMemberIdsContaining("noUser");

        // Assert: lista vacía
        assertTrue(result.isEmpty());
    }
}