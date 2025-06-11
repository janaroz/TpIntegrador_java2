package com.alkemy.java2.TPIntegrador.DTOs;

import com.alkemy.java2.TPIntegrador.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class UserRegisterDTO {
    private String id;
    private String fullName;
    private String username;
    private String email;
    private String password;
    private Set<Role> role;
    private String profileImageUrl;
}
