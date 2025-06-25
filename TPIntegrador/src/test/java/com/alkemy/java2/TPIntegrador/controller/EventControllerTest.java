package com.alkemy.java2.TPIntegrador.controller;

import com.alkemy.java2.TPIntegrador.DTOs.EventDTO;
import com.alkemy.java2.TPIntegrador.DTOs.ParticipantDTO;
import com.alkemy.java2.TPIntegrador.service.EventService;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventControllerTest {

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventController controller;

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
        EventDTO dto = new EventDTO();
        EventDTO saved = new EventDTO();
        when(eventService.createEvent(dto)).thenReturn(saved);

        // Act
        EventDTO result = controller.create(dto);

        // Assert
        assertNotNull(result);
        verify(eventService).createEvent(dto);
    }

    @Test
    void get_HappyPath() {
        // Arrange
        String id = "1";
        EventDTO dto = new EventDTO();
        when(eventService.getEventById(id)).thenReturn(dto);

        // Act
        EventDTO result = controller.get(id);

        // Assert
        assertNotNull(result);
        verify(eventService).getEventById(id);
    }

    @Test
    void get_NotFound() {
        // Arrange
        String id = "2";
        when(eventService.getEventById(id)).thenReturn(null);

        // Act
        EventDTO result = controller.get(id);

        // Assert
        assertNull(result);
        verify(eventService).getEventById(id);
    }

    @Test
    void byGroup_HappyPath() {
        // Arrange
        String groupId = "g1";
        List<EventDTO> list = List.of(new EventDTO());
        when(eventService.getEventsByGroup(groupId)).thenReturn(list);

        // Act
        List<EventDTO> result = controller.byGroup(groupId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(eventService).getEventsByGroup(groupId);
    }

    @Test
    void byGroup_EmptyList() {
        // Arrange
        String groupId = "g2";
        when(eventService.getEventsByGroup(groupId)).thenReturn(Collections.emptyList());

        // Act
        List<EventDTO> result = controller.byGroup(groupId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void byGroupAsync_HappyPath() throws Exception {
        // Arrange
        String groupId = "g1";
        List<EventDTO> list = List.of(new EventDTO());
        CompletableFuture<List<EventDTO>> future = CompletableFuture.completedFuture(list);
        when(eventService.getEventsByGroupAsync(groupId)).thenReturn(future);

        // Act
        CompletableFuture<List<EventDTO>> resultFuture = controller.byGroupAsync(groupId);
        List<EventDTO> result = resultFuture.get(1, TimeUnit.SECONDS);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(eventService).getEventsByGroupAsync(groupId);
    }

    @Test
    void byCreator_HappyPath() {
        // Arrange
        String userId = "u1";
        List<EventDTO> list = List.of(new EventDTO());
        when(eventService.getEventsByCreator(userId)).thenReturn(list);

        // Act
        List<EventDTO> result = controller.byCreator(userId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(eventService).getEventsByCreator(userId);
    }

    @Test
    void update_HappyPath() {
        // Arrange
        String id = "1";
        EventDTO dto = new EventDTO();
        EventDTO updated = new EventDTO();
        when(eventService.updateEvent(id, dto)).thenReturn(updated);

        // Act
        EventDTO result = controller.update(id, dto);

        // Assert
        assertNotNull(result);
        verify(eventService).updateEvent(id, dto);
    }

    @Test
    void delete_HappyPath() {
        // Arrange
        String id = "1";
        doNothing().when(eventService).deleteEvent(id);

        // Act
        controller.delete(id);

        // Assert
        verify(eventService).deleteEvent(id);
    }

    @Test
    void addParticipant_HappyPath() {
        // Arrange
        String id = "1";
        ParticipantDTO p = new ParticipantDTO();
        EventDTO dto = new EventDTO();
        when(eventService.addParticipant(id, p)).thenReturn(dto);

        // Act
        EventDTO result = controller.addParticipant(id, p);

        // Assert
        assertNotNull(result);
        verify(eventService).addParticipant(id, p);
    }

    @Test
    void processGroups_HappyPath() {
        // Arrange
        List<String> groupIds = List.of("g1", "g2");
        doNothing().when(eventService).processEventsByMultipleGroups(groupIds);

        // Act
        String result = controller.processGroups(groupIds);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Procesamiento paralelo iniciado"));
        verify(eventService).processEventsByMultipleGroups(groupIds);
    }

    @Test
    void processGroups_EmptyList() {
        // Arrange
        List<String> groupIds = Collections.emptyList();
        doNothing().when(eventService).processEventsByMultipleGroups(groupIds);

        // Act
        String result = controller.processGroups(groupIds);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("0"));
        verify(eventService).processEventsByMultipleGroups(groupIds);
    }
}