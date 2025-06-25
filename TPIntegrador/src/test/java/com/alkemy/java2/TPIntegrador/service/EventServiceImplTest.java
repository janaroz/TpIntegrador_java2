package com.alkemy.java2.TPIntegrador.service;

import com.alkemy.java2.TPIntegrador.DTOs.EventDTO;
import com.alkemy.java2.TPIntegrador.DTOs.ParticipantDTO;
import com.alkemy.java2.TPIntegrador.mappers.GenericMapper;
import com.alkemy.java2.TPIntegrador.model.Event;
import com.alkemy.java2.TPIntegrador.model.Participant;
import com.alkemy.java2.TPIntegrador.repository.EventRepository;
import com.alkemy.java2.TPIntegrador.service.impl.EventServiceImpl;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventServiceImplTest {

    @Mock
    private EventRepository repo;
    @Mock
    private GenericMapper mapper;

    @InjectMocks
    private EventServiceImpl service;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        service = new EventServiceImpl(repo, mapper);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void createEvent_HappyPath() {
        // Arrange
        EventDTO dto = new EventDTO();
        Event event = new Event();
        Event savedEvent = new Event();
        EventDTO resultDto = new EventDTO();
        resultDto.setId("1");

        when(mapper.toEntity(dto, Event.class)).thenReturn(event);
        when(repo.save(event)).thenReturn(savedEvent);
        when(mapper.toDTO(savedEvent, EventDTO.class)).thenReturn(resultDto);

        // Act
        EventDTO result = service.createEvent(dto);

        // Assert
        assertNotNull(result);
        assertEquals("1", result.getId());
        verify(repo).save(event);
    }

    @Test
    void getEventById_Found() {
        // Arrange
        String id = "1";
        Event event = new Event();
        event.setTitle("Test");
        EventDTO dto = new EventDTO();
        when(repo.findById(id)).thenReturn(Optional.of(event));
        when(mapper.toDTO(event, EventDTO.class)).thenReturn(dto);

        // Act
        EventDTO result = service.getEventById(id);

        // Assert
        assertNotNull(result);
        verify(repo).findById(id);
    }

    @Test
    void getEventById_NotFound() {
        // Arrange
        String id = "2";
        when(repo.findById(id)).thenReturn(Optional.empty());

        // Act
        EventDTO result = service.getEventById(id);

        // Assert
        assertNull(result);
    }

    @Test
    void getEventsByGroup_ReturnsList() {
        // Arrange
        String groupId = "g1";
        Event event = new Event();
        EventDTO dto = new EventDTO();
        when(repo.findByGroupId(groupId)).thenReturn(List.of(event));
        when(mapper.toDTO(event, EventDTO.class)).thenReturn(dto);

        // Act
        List<EventDTO> result = service.getEventsByGroup(groupId);

        // Assert
        assertEquals(1, result.size());
        verify(repo).findByGroupId(groupId);
    }

    @Test
    void getEventsByGroup_EmptyList() {
        // Arrange
        String groupId = "g2";
        when(repo.findByGroupId(groupId)).thenReturn(Collections.emptyList());

        // Act
        List<EventDTO> result = service.getEventsByGroup(groupId);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void getEventsByGroupAsync_HappyPath() throws Exception {
        // Arrange
        String groupId = "g1";
        Event event = new Event();
        EventDTO dto = new EventDTO();
        when(repo.findByGroupId(groupId)).thenReturn(List.of(event));
        when(mapper.toDTO(event, EventDTO.class)).thenReturn(dto);

        SecurityContext context = mock(SecurityContext.class);
        SecurityContextHolder.setContext(context);

        // Act
        CompletableFuture<List<EventDTO>> future = service.getEventsByGroupAsync(groupId);
        List<EventDTO> result = future.get(2, TimeUnit.SECONDS);

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    void getEventsByCreator_ReturnsList() {
        // Arrange
        String creatorId = "u1";
        Event event = new Event();
        EventDTO dto = new EventDTO();
        when(repo.findByCreatorId(creatorId)).thenReturn(List.of(event));
        when(mapper.toDTO(event, EventDTO.class)).thenReturn(dto);

        // Act
        List<EventDTO> result = service.getEventsByCreator(creatorId);

        // Assert
        assertEquals(1, result.size());
        verify(repo).findByCreatorId(creatorId);
    }

    @Test
    void updateEvent_Found() {
        // Arrange
        String id = "1";
        EventDTO dto = new EventDTO();
        dto.setTitle("Nuevo");
        dto.setDescription("Desc");
        dto.setEventDate(LocalDateTime.now());
        dto.setLocation("Loc");

        Event event = new Event();
        when(repo.findById(id)).thenReturn(Optional.of(event));
        when(repo.save(event)).thenReturn(event);
        when(mapper.toDTO(event, EventDTO.class)).thenReturn(dto);

        // Act
        EventDTO result = service.updateEvent(id, dto);

        // Assert
        assertNotNull(result);
        assertEquals("Nuevo", result.getTitle());
    }

    @Test
    void updateEvent_NotFound() {
        // Arrange
        String id = "2";
        EventDTO dto = new EventDTO();
        when(repo.findById(id)).thenReturn(Optional.empty());

        // Act
        EventDTO result = service.updateEvent(id, dto);

        // Assert
        assertNull(result);
    }

    @Test
    void deleteEvent_Deletes() {
        // Arrange
        String id = "1";
        doNothing().when(repo).deleteById(id);

        // Act
        service.deleteEvent(id);

        // Assert
        verify(repo).deleteById(id);
    }

    @Test
    void addParticipant_Found() {
        // Arrange
        String id = "1";
        ParticipantDTO pDto = new ParticipantDTO();
        Participant p = new Participant();
        Event event = new Event();
        event.setParticipants(new ArrayList<>());
        EventDTO dto = new EventDTO();
        dto.setParticipants(new ArrayList<>(List.of(pDto)));

        when(repo.findById(id)).thenReturn(Optional.of(event));
        when(mapper.toEntity(pDto, Participant.class)).thenReturn(p);
        when(repo.save(event)).thenReturn(event);
        when(mapper.toDTO(event, EventDTO.class)).thenReturn(dto);

        // Act
        EventDTO result = service.addParticipant(id, pDto);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getParticipants().size());
    }

    @Test
    void addParticipant_NotFound() {
        // Arrange
        String id = "2";
        ParticipantDTO pDto = new ParticipantDTO();
        when(repo.findById(id)).thenReturn(Optional.empty());

        // Act
        EventDTO result = service.addParticipant(id, pDto);

        // Assert
        assertNull(result);
    }


}