package com.alkemy.java2.TPIntegrador.config;

import com.alkemy.java2.TPIntegrador.model.Event;
import com.alkemy.java2.TPIntegrador.model.Group;
import com.alkemy.java2.TPIntegrador.model.User;
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
import java.time.ZoneOffset;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataLoaderConfig implements CommandLineRunner {

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
        User juan = createUser("juancito", "juan.perez@gmail.com", "123456", "Juan Pérez");
        User flor = createUser("flor", "florencia.lopez@gmail.com", "123456", "Florencia López");
        User mati = createUser("matimessi", "mati.messi@gmail.com", "123456", "Matías Messi");
        User lau = createUser("lauu", "laura.garcia@gmail.com", "123456", "Laura García");

        log.info("Usuarios creados: {}, {}, {}, {}", juan.getUsername(), flor.getUsername(), mati.getUsername(), lau.getUsername());

        log.info("Creando grupos...");
        Group asadoTeam = createGroup("Los del Asado", "Grupo para organizar asaditos", juan, List.of(flor, mati));
        Group futbol5 = createGroup("Fútbol 5", "Los martes a las 20:00hs en Palermo", flor, List.of(juan, lau));

        log.info("Grupos creados: {}, {}", asadoTeam.getName(), futbol5.getName());

        log.info("Creando eventos...");
        createEvent("Asado en lo de Juan", "Traer bebida. Parrilla a las 13hs.", asadoTeam, juan, LocalDateTime.now().plusDays(3));
        createEvent("Fútbol en Palermo", "Cancha reservada. $1500 cada uno.", futbol5, flor, LocalDateTime.now().plusDays(5));
        createEvent("Salida al cine", "Vamos al cine Gaumont a ver cine nacional", futbol5, lau, LocalDateTime.now().plusDays(7));
        log.info("Eventos creados.");
    }

    private User createUser(String username, String email, String password, String fullName) {
        User user = userRepository.save(User.builder()
                .username(username)
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .fullName(fullName)
                .createdAt(Instant.now())
                .build());
        log.debug("Usuario creado: {}", user);
        return user;
    }

    private Group createGroup(String name, String description, User owner, List<User> members) {
        Group group = Group.builder()
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

    private Event createEvent(String title, String description, Group group, User organizer, LocalDateTime dateTime) {
        Event event = Event.builder()
                .title(title)
                .description(description)
                .groupId(group.getId())
                .organizerId(organizer.getId())
                .eventDate(dateTime.toInstant(ZoneOffset.UTC))
                .createdAt(Instant.now())
                .build();
        Event savedEvent = eventRepository.save(event);
        log.debug("Evento creado: {}", savedEvent);
        return savedEvent;
    }
}