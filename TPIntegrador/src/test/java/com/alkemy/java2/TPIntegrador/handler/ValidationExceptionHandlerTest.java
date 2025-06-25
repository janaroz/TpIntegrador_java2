package com.alkemy.java2.TPIntegrador.handler;

import com.alkemy.java2.TPIntegrador.exceptions.ResourceNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ValidationExceptionHandlerTest {

    private final ValidationExceptionHandler handler = new ValidationExceptionHandler();

    @Test
    void handleValidationExceptions_ReturnsFieldErrors() {
        // Arrange
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError error1 = new FieldError("obj", "field1", "msg1");
        FieldError error2 = new FieldError("obj", "field2", "msg2");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(error1, error2));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        // Act
        ResponseEntity<?> response = handler.handleValidationExceptions(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body);
        assertEquals("msg1", body.get("field1"));
        assertEquals("msg2", body.get("field2"));
    }

    @Test
    void handleBadRequest_ReturnsBadRequest() {
        // Arrange
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("error");

        // Act
        ResponseEntity<?> response = handler.handleBadRequest(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("Request malformado o datos inválidos", body.get("error"));
    }

    @Test
    void handleNotFound_ReturnsNotFound() {
        // Arrange
        ResourceNotFoundException ex = new ResourceNotFoundException("No encontrado");

        // Act
        ResponseEntity<?> response = handler.handleNotFound(ex);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("No encontrado", body.get("error"));
    }

    @Test
    void handleAccessDenied_ReturnsForbidden() {
        // Arrange
        AccessDeniedException ex = new AccessDeniedException("denegado");

        // Act
        ResponseEntity<?> response = handler.handleAccessDenied(ex);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("No tienes permisos para realizar esta acción", body.get("error"));
    }

    @Test
    void handleAuthentication_ReturnsUnauthorized() {
        // Arrange
        AuthenticationException ex = mock(AuthenticationException.class);

        // Act
        ResponseEntity<?> response = handler.handleAuthentication(ex);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("No autenticado o sesión expirada", body.get("error"));
    }

    @Test
    void handleConstraintViolation_ReturnsBadRequest() {
        // Arrange
        ConstraintViolationException ex = new ConstraintViolationException("violación", null);

        // Act
        ResponseEntity<?> response = handler.handleConstraintViolation(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("violación", body.get("error"));
    }
}