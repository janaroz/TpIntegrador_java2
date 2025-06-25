package com.alkemy.java2.TPIntegrador.repository.unitarios;

import com.alkemy.java2.TPIntegrador.model.Event;
import com.alkemy.java2.TPIntegrador.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventRepositoryUnitTest {

    @Mock
    private EventRepository eventRepository;

    @Test
    void testFindByGroupId1() {
        List<Event> events = List.of(Event.builder().groupId("group1").title("Evento 1").build());
        when(eventRepository.findByGroupId("group1")).thenReturn(events);

        List<Event> result = eventRepository.findByGroupId("group1");

        assertEquals(1, result.size());
        assertEquals("Evento 1", result.get(0).getTitle());
        verify(eventRepository).findByGroupId("group1");
    }
    @Test
    void testFindByGroupId2() {
        Event event = Event.builder().groupId("grp123").build();
        when(eventRepository.findByGroupId("grp123")).thenReturn(List.of(event));

        List<Event> result = eventRepository.findByGroupId("grp123");
        assertEquals(1, result.size());
    }

    @Test
    void testFindByCreatorId() {
        Event event = Event.builder().creatorId("user123").build();
        when(eventRepository.findByCreatorId("user123")).thenReturn(List.of(event));

        List<Event> result = eventRepository.findByCreatorId("user123");
        assertFalse(result.isEmpty());
    }

    @Test
    void testFindByEventDateBetween() {
        Instant now = Instant.now();
        Event event = Event.builder().createdAt(now).build();
        when(eventRepository.findByEventDateBetween(now.minusSeconds(10), now.plusSeconds(10)))
                .thenReturn(List.of(event));

        List<Event> result = eventRepository.findByEventDateBetween(now.minusSeconds(10), now.plusSeconds(10));
        assertEquals(1, result.size());
    }


    @Test
    void testSaveEvent_Success() {
        // Arrange: crear evento y mockear save
        Event event = Event.builder().id("1").title("Evento Test").build();
        when(eventRepository.save(event)).thenReturn(event);

        // Act: guardar evento
        Event saved = eventRepository.save(event);

        // Assert: verificar guardado correcto
        assertNotNull(saved);
        assertEquals("Evento Test", saved.getTitle());
        verify(eventRepository).save(event);
    }



    @Test
    void testFindById_EventExists() {
        // Arrange: mockear búsqueda por id existente
        Event event = Event.builder().id("2").title("Evento 2").build();
        when(eventRepository.findById("2")).thenReturn(Optional.of(event));

        // Act: buscar evento
        Optional<Event> result = eventRepository.findById("2");

        // Assert: evento encontrado
        assertTrue(result.isPresent());
        assertEquals("Evento 2", result.get().getTitle());
    }

    @Test
    void testFindById_EventNotFound() {
        // Arrange: mockear búsqueda por id inexistente
        when(eventRepository.findById("3")).thenReturn(Optional.empty());

        // Act: buscar evento
        Optional<Event> result = eventRepository.findById("3");

        // Assert: evento no encontrado
        assertFalse(result.isPresent());
        assertNull(result.orElse(null));
    }

    @Test
    void testFindAll_EmptyList() {
        // Arrange: mockear lista vacía
        when(eventRepository.findAll()).thenReturn(List.of());

        // Act: buscar todos
        List<Event> events = eventRepository.findAll();

        // Assert: lista vacía
        assertTrue(events.isEmpty());
    }

    @Test
    void testFindAll_WithEvents() {
        // Arrange: mockear lista con eventos
        Event e1 = Event.builder().id("1").build();
        Event e2 = Event.builder().id("2").build();
        when(eventRepository.findAll()).thenReturn(List.of(e1, e2));

        // Act: buscar todos
        List<Event> events = eventRepository.findAll();

        // Assert: lista con dos eventos
        assertEquals(2, events.size());
        assertNotNull(events.get(0));
    }

    @Test
    void testDeleteById_Success() {
        // Arrange: mockear borrado exitoso
        String eventId = "4";
        doNothing().when(eventRepository).deleteById(eventId);

        // Act: borrar evento
        eventRepository.deleteById(eventId);

        // Assert: verificar borrado
        verify(eventRepository).deleteById(eventId);
    }


    @Test
    void testExistsById_True() {
        // Arrange: mockear existencia
        when(eventRepository.existsById("5")).thenReturn(true);

        // Act: verificar existencia
        boolean exists = eventRepository.existsById("5");

        // Assert: debe existir
        assertTrue(exists);
    }

    @Test
    void testExistsById_False() {
        // Arrange: mockear no existencia
        when(eventRepository.existsById("6")).thenReturn(false);

        // Act: verificar existencia
        boolean exists = eventRepository.existsById("6");

        // Assert: no debe existir
        assertFalse(exists);
    }

    @Test
    void testFindByGroupId_Empty() {
        // Arrange: mockear grupo sin eventos
        when(eventRepository.findByGroupId("noGroup")).thenReturn(List.of());

        // Act: buscar por grupo
        List<Event> result = eventRepository.findByGroupId("noGroup");

        // Assert: lista vacía
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByEventDateBetween_NoEvents() {
        // Arrange: mockear sin eventos en rango
        Instant start = Instant.now();
        Instant end = start.plusSeconds(1000);
        when(eventRepository.findByEventDateBetween(start, end)).thenReturn(List.of());

        // Act: buscar por rango de fechas
        List<Event> result = eventRepository.findByEventDateBetween(start, end);

        // Assert: lista vacía
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByCreatorId_Null() {
        // Arrange: mockear búsqueda con id null
        when(eventRepository.findByCreatorId(null)).thenThrow(IllegalArgumentException.class);

        // Act & Assert: debe lanzar excepción
        assertThrows(IllegalArgumentException.class, () -> eventRepository.findByCreatorId(null));
    }
}