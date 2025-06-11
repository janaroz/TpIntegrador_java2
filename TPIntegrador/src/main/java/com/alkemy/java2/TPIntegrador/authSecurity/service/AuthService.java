package com.alkemy.java2.TPIntegrador.authSecurity.service;


import com.alkemy.java2.TPIntegrador.DTOs.AuthRequest;
import com.alkemy.java2.TPIntegrador.DTOs.AuthResponse;
import com.alkemy.java2.TPIntegrador.DTOs.UserLogInDTO;

public interface AuthService {
    AuthResponse register(UserLogInDTO request);
    AuthResponse authenticate(AuthRequest request);
}