package com.alkemy.java2.TPIntegrador.authSecurity.service;


import com.alkemy.java2.TPIntegrador.DTOs.AuthRequest;
import com.alkemy.java2.TPIntegrador.DTOs.AuthResponse;
import com.alkemy.java2.TPIntegrador.DTOs.UserRegisterDTO;
import com.alkemy.java2.TPIntegrador.mappers.GenericMapper;
import com.alkemy.java2.TPIntegrador.model.User;
import com.alkemy.java2.TPIntegrador.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j // Lombok annotation for logger
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final GenericMapper genericMapper;

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(UserRegisterDTO request) {
        log.debug("Intentando registrar nuevo usuario: {}", request.getUsername());


        // Changed to use our custom exists method
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Username {} already exists", request.getUsername());
            //throw new IllegalArgumentException("Username already exists");
        }
        User user = genericMapper.toEntity(request, User.class);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        //user.setRoles(request.getRoles());

        User savedUser = userRepository.save(user);
        log.info("Nuevo usuario registrado con ID: {}", savedUser.getId());

        String jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    @Override
    public AuthResponse authenticate(AuthRequest request) {
        log.debug("Autenticando usuario: {}", request.getUsername());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> {
                        log.error("Usuario no encontrado después de autenticación exitosa: {}", request.getUsername());
                        return new UsernameNotFoundException("User not found");
                    });

            String jwtToken = jwtService.generateToken(user);
            log.info("Usuario {} autenticado exitosamente", user.getUsername());
            user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .forEach(authority -> log.info("User authority: {}", authority));

            return AuthResponse.builder()
                    .token(jwtToken)
                    .build();

        } catch (AuthenticationException e) {
            log.warn("Falló autenticación para usuario: {}", request.getUsername());
            throw new BadCredentialsException("Invalid credentials");
        }
    }
}