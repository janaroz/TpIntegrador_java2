package com.alkemy.java2.TPIntegrador.service.impl;

import com.alkemy.java2.TPIntegrador.DTOs.UserDTO;
import com.alkemy.java2.TPIntegrador.mappers.GenericMapper;
import com.alkemy.java2.TPIntegrador.model.User;
import com.alkemy.java2.TPIntegrador.repository.UserRepository;
import com.alkemy.java2.TPIntegrador.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repo;
    private final GenericMapper mapper;

    public UserDTO createUser(UserDTO dto) {
        User user = mapper.toEntity(dto, User.class);
        if (repo.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        repo.save(user);
        return mapper.toDTO(user, UserDTO.class);
    }
    public UserDTO getUserById(String id) {
        return repo.findById(id)
            .map(user -> mapper.toDTO(user, UserDTO.class))
            .orElse(null);
    }
    public List<UserDTO> getAllUsers() {
        return repo.findAll()
            .stream()
            .map(user -> mapper.toDTO(user, UserDTO.class))
            .collect(toList());
    }
    public UserDTO updateUser(UserDTO dto) {
        return repo.findById(dto.getId()).map(u -> {
            u.setFullName(dto.getFullName());
            u.setProfileImageUrl(dto.getProfileImageUrl());
            u.setEmail(dto.getEmail());
            return mapper.toDTO(repo.save(u), UserDTO.class);
        }).orElse(null);
    }
    public void deleteUser(String id) { repo.deleteById(id); }
}