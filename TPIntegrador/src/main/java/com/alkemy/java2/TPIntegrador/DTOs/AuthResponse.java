package com.alkemy.java2.TPIntegrador.DTOs;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String token;
}
