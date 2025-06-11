package com.alkemy.java2.TPIntegrador.service.impl;

import com.alkemy.java2.TPIntegrador.DTOs.GroupDTO;
import com.alkemy.java2.TPIntegrador.DTOs.UserDTO;
import com.alkemy.java2.TPIntegrador.mappers.GenericMapper;
import com.alkemy.java2.TPIntegrador.model.Group;
import com.alkemy.java2.TPIntegrador.repository.GroupRepository;
import com.alkemy.java2.TPIntegrador.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GenericMapper genericMapper;

    @Override
    public GroupDTO createGroup(GroupDTO groupDTO) {
        Group group = genericMapper.toEntity(groupDTO, Group.class);
        group = groupRepository.save(group);
        return genericMapper.toDTO(group, GroupDTO.class);
    }

    @Override
    public GroupDTO getGroupById(String id) {
        Optional<Group> groupOpt = groupRepository.findById(id);
        return groupOpt.map(group -> genericMapper.toDTO(group, GroupDTO.class)).orElse(null);
    }

    @Override
    public List<GroupDTO> getAllGroups() {
        return groupRepository.findAll()
                .stream()
                .map(group -> genericMapper.toDTO(group, GroupDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public GroupDTO addMemberToGroup(String groupId, UserDTO userDTO) {
        Optional<Group> groupOpt = groupRepository.findById(groupId);
        if (groupOpt.isPresent()) {
            Group group = groupOpt.get();
            List<String> memberIds = group.getMemberIds() != null ?
                    new java.util.ArrayList<>(group.getMemberIds()) : new java.util.ArrayList<>();
            memberIds.add(userDTO.getId());
            group.setMemberIds(memberIds);
            group = groupRepository.save(group);
            return genericMapper.toDTO(group, GroupDTO.class);
        }
        return null;
    }
}