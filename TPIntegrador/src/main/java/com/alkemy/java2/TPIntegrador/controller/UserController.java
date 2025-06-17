package com.alkemy.java2.TPIntegrador.controller;

import com.alkemy.java2.TPIntegrador.DTOs.UserDTO;
import com.alkemy.java2.TPIntegrador.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService svc;

    @PostMapping
    public UserDTO create(@RequestBody UserDTO dto) {
        return svc.createUser(dto);
    }

    @GetMapping("/{id}")
    public UserDTO get(@PathVariable String id) {
        return svc.getUserById(id);
    }

    @GetMapping
    public List<UserDTO> all() {
        return svc.getAllUsers();
    }

    @PutMapping("/")
    public UserDTO update( @RequestBody UserDTO dto) {
        return svc.updateUser( dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        svc.deleteUser(id);
    }

    @GetMapping("/async")
    public CompletableFuture<List<UserDTO>> getAllUsersAsync() {
        return svc.getAllUsersAsync();
    }

    @PostMapping("/process")
    public String processUsers(@RequestBody List<String> ids) {
        svc.processUsersByIdList(ids);
        return "Procesamiento paralelo iniciado para " + ids.size() + " usuarios.";
    }
}