package com.alkemy.java2.TPIntegrador.DTOs;

import com.alkemy.java2.TPIntegrador.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDTO {
    private String id;

    @NotBlank
    private String fullName;

    @NotBlank
    private String username;

    @Email
    @NotBlank
    private String email;

    @Size(min = 8, max = 20)
    @NotBlank
    private String password;

    @NotEmpty
    private Set<Role> role;

    @NotBlank
    private String profileImageUrl;
}