package com.alkemy.java2.TPIntegrador.authSecurity.controller;


import com.alkemy.java2.TPIntegrador.DTOs.AuthRequest;
import com.alkemy.java2.TPIntegrador.DTOs.AuthResponse;
import com.alkemy.java2.TPIntegrador.DTOs.UserLogInDTO;
import com.alkemy.java2.TPIntegrador.authSecurity.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;
  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserLogInDTO request) {
    return ResponseEntity.ok(authService.register(request));}
  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
    return ResponseEntity.ok(authService.authenticate(request));   }}
