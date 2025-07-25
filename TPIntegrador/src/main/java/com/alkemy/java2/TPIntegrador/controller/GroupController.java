package com.alkemy.java2.TPIntegrador.controller;

import com.alkemy.java2.TPIntegrador.DTOs.GroupDTO;
import com.alkemy.java2.TPIntegrador.DTOs.UserDTO;
import com.alkemy.java2.TPIntegrador.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<GroupDTO> createGroup(@RequestBody GroupDTO groupDTO) {
        GroupDTO saved = groupService.createGroup(groupDTO);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupDTO> getGroup(@PathVariable String id) {
        GroupDTO group = groupService.getGroupById(id);
        return ResponseEntity.ok(group);
    }

    @PostMapping("/{id}/add-member")
    public ResponseEntity<GroupDTO> addMember(@PathVariable String id, @RequestBody UserDTO userDTO) {
        GroupDTO updated = groupService.addMemberToGroup(id, userDTO);
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    public ResponseEntity<List<GroupDTO>> getAllGroups() {
        List<GroupDTO> groups = groupService.getAllGroups();
        return ResponseEntity.ok(groups);
    }
    @GetMapping("/async")
    public CompletableFuture<ResponseEntity<List<GroupDTO>>> getAllGroupsAsync() {
        return groupService.getAllGroupsAsync().thenApply(ResponseEntity::ok);
    }

    @PostMapping("/process")
    public ResponseEntity<String> processGroups(@RequestBody List<String> ids) {
        groupService.processMultipleGroups(ids);
        return ResponseEntity.ok("Procesamiento en paralelo iniciado para " + ids.size() + " grupos.");
    }
}