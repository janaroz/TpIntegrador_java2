package com.alkemy.java2.TPIntegrador.service;

import com.alkemy.java2.TPIntegrador.DTOs.GroupDTO;
import com.alkemy.java2.TPIntegrador.DTOs.UserDTO;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface GroupService {
    GroupDTO createGroup(GroupDTO groupDTO);
    GroupDTO getGroupById(String id);
    List<GroupDTO> getAllGroups();
    GroupDTO addMemberToGroup(String groupId, UserDTO userDTO);


    CompletableFuture<List<GroupDTO>> getAllGroupsAsync();


    void processMultipleGroups(List<String> ids);
}
