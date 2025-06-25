package com.alkemy.java2.TPIntegrador.exceptions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResourceNotFoundExceptionTest {

    @Test
    void constructor_SetsMessage() {
        // Arrange
        String message = "Recurso no encontrado";

        // Act
        ResourceNotFoundException ex = new ResourceNotFoundException(message);

        // Assert
        assertEquals(message, ex.getMessage());
    }

    @Test
    void isRuntimeException() {
        // Act
        ResourceNotFoundException ex = new ResourceNotFoundException("msg");

        // Assert
        assertTrue(ex instanceof RuntimeException);
    }
}