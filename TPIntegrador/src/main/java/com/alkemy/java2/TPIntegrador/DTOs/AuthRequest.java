package com.alkemy.java2.TPIntegrador.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthRequest {
    @NotBlank
    private String username;
    @NotBlank
    @Size(min = 8, max = 20, message = "8 y 10 c")
    private String password;
}