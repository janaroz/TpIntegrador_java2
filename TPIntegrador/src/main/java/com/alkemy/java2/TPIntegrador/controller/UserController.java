package com.alkemy.java2.TPIntegrador.controller;

import com.alkemy.java2.TPIntegrador.DTOs.UserDTO;
import com.alkemy.java2.TPIntegrador.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService svc;

    @PostMapping
    public UserDTO create(@RequestBody UserDTO dto) { return svc.createUser(dto); }

    @GetMapping("/{id}")
    public UserDTO get(@PathVariable String id) { return svc.getUserById(id); }

    @GetMapping
    public List<UserDTO> all() { return svc.getAllUsers(); }

    @PutMapping("/")
    public UserDTO update( @RequestBody UserDTO dto) {
        return svc.updateUser( dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) { svc.deleteUser(id); }
}