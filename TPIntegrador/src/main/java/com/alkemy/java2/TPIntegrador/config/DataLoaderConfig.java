package com.alkemy.java2.TPIntegrador.config;

import com.alkemy.java2.TPIntegrador.DTOs.UserRegisterDTO;
import com.alkemy.java2.TPIntegrador.authSecurity.service.AuthServiceImpl;
import com.alkemy.java2.TPIntegrador.model.Event;
import com.alkemy.java2.TPIntegrador.model.Group;
import com.alkemy.java2.TPIntegrador.model.User;
import com.alkemy.java2.TPIntegrador.model.enums.Role;
import com.alkemy.java2.TPIntegrador.repository.EventRepository;
import com.alkemy.java2.TPIntegrador.repository.GroupRepository;
import com.alkemy.java2.TPIntegrador.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataLoaderConfig implements CommandLineRunner {
    private final AuthServiceImpl authService;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final EventRepository eventRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("Eliminando datos previos...");
        userRepository.deleteAll();
        groupRepository.deleteAll();
        eventRepository.deleteAll();

        log.info("Creando usuarios...");
        User juan = createUser("u1", "juancito", "juan.perez@gmail.com", "12345678", "Juan Pérez", "img1.jpg", Set.of(Role.USER));
        User flor = createUser("u2", "flor", "florencia.lopez@gmail.com", "12345678", "Florencia López", "img2.jpg", Set.of(Role.USER));
        User mati = createUser("u3", "matimessi", "mati.messi@gmail.com", "12345678", "Matías Messi", "img3.jpg", Set.of(Role.USER));
        User lau  = createUser("u4", "lauu", "laura.garcia@gmail.com", "12345678", "Laura García", "img4.jpg", Set.of(Role.USER));
        log.info("Usuarios creados: {}, {}, {}, {}", juan.getUsername(), flor.getUsername(), mati.getUsername(), lau.getUsername());

        log.info("Creando grupos...");
        Group asadoTeam = createGroup("g1", "Los del Asado", "Grupo para organizar asaditos", juan, List.of(flor, mati));
        Group futbol5 = createGroup("g2", "Fútbol 5", "Los martes a las 20:00hs en Palermo", flor, List.of(juan, lau));

        log.info("Grupos creados: {}, {}", asadoTeam.getName(), futbol5.getName());

        log.info("Creando eventos...");
        createEvent("e1", "Asado en lo de Juan", "Traer bebida. Parrilla a las 13hs.", asadoTeam, juan, LocalDateTime.now().plusDays(3));
        createEvent("e2", "Fútbol en Palermo", "Cancha reservada. $1500 cada uno.", futbol5, flor, LocalDateTime.now().plusDays(5));
        createEvent("e3", "Salida al cine", "Vamos al cine Gaumont a ver cine nacional", futbol5, lau, LocalDateTime.now().plusDays(7));
        log.info("Eventos creados.");
    }

    private User createUser(String id, String username, String email, String password, String fullName, String profileImageUrl, Set<Role> roles) {
        UserRegisterDTO dto = UserRegisterDTO.builder()
                .id(id)
                .username(username)
                .email(email)
                .password(password)
                .fullName(fullName)
                .profileImageUrl(profileImageUrl)
                .role(roles)
                .build();
        authService.register(dto);
        // Recuperar el usuario recién creado para devolverlo
        User user = userRepository.findByUsername(username).orElseThrow();
        log.debug("Usuario creado: {}", user);
        return user;
    }

    private Group createGroup(String id, String name, String description, User owner, List<User> members) {
        Group group = Group.builder()
                .id(id)
                .name(name)
                .description(description)
                .ownerId(owner.getId())
                .memberIds(members.stream().map(User::getId).toList())
                .createdAt(Instant.now())
                .build();
        Group savedGroup = groupRepository.save(group);
        log.debug("Grupo creado: {}", savedGroup);
        return savedGroup;
    }

    private Event createEvent(String id, String title, String description, Group group, User organizer, LocalDateTime dateTime) {
        Event event = Event.builder()
                .id(id)
                .title(title)
                .description(description)
                .groupId(group.getId())
                .organizerId(organizer.getId())
                .eventDate(dateTime)
                .createdAt(Instant.now())
                .build();
        Event savedEvent = eventRepository.save(event);
        log.debug("Evento creado: {}", savedEvent);
        return savedEvent;
    }
}