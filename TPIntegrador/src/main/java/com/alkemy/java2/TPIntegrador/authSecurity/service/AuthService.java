package com.alkemy.java2.TPIntegrador.authSecurity.service;


import com.alkemy.java2.TPIntegrador.DTOs.AuthRequest;
import com.alkemy.java2.TPIntegrador.DTOs.AuthResponse;
import com.alkemy.java2.TPIntegrador.DTOs.UserRegisterDTO;

public interface AuthService {
    AuthResponse register(UserRegisterDTO request);
    AuthResponse authenticate(AuthRequest request);
}