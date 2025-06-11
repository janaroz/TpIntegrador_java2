package com.alkemy.java2.TPIntegrador.service;

import com.alkemy.java2.TPIntegrador.DTOs.UserDTO;

import java.util.List;

public interface UserService {
    UserDTO createUser(UserDTO dto);
    UserDTO getUserById(String id);
    List<UserDTO> getAllUsers();
    UserDTO updateUser( UserDTO dto);
    void deleteUser(String id);
}
