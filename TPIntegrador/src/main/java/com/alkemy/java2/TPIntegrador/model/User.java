package com.alkemy.java2.TPIntegrador.model;

import com.alkemy.java2.TPIntegrador.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {
    @Id
    private String id;

    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String passwordHash;

    @NotBlank
    private String fullName;

    private String profileImageUrl;

    private List<String> groupIds;

    private boolean active = true;

    @Field("roles")
    private Set<Role> roles = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    // Refactor para evitar duplicación
    private boolean isUserActive() {
        return active;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isUserActive();
    }

    @Override
    public boolean isAccountNonLocked() {
        return isUserActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isUserActive();
    }

    @Override
    public boolean isEnabled() {
        return isUserActive();
    }

    @Builder.Default
    private Instant createdAt = Instant.now();
}