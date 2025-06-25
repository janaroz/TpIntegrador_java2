package com.alkemy.java2.TPIntegrador.config;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.*;

class MapperConfigTest {

    @Test
    void testModelMapperBean() {
        // Arrange
        MapperConfig config = new MapperConfig();

        // Act
        ModelMapper mapper = config.modelMapper();

        // Assert
        assertNotNull(mapper);
        assertEquals(ModelMapper.class, mapper.getClass());
    }
}