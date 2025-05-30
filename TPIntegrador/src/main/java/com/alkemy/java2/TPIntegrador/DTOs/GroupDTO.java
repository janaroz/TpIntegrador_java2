package com.alkemy.java2.TPIntegrador.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupDTO {
    private String name;
    private String description;
    private boolean isPublic;
    private String ownerId;
    private List<String> memberIds;
}