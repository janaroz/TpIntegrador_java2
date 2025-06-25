package com.alkemy.java2.TPIntegrador.mappers;

import org.junit.jupiter.api.*;
import org.modelmapper.ModelMapper;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GenericMapperTest {

    private ModelMapper modelMapper;
    private GenericMapper genericMapper;

    @BeforeEach
    void setUp() {
        modelMapper = mock(ModelMapper.class);
        genericMapper = new GenericMapper(modelMapper);
    }

    @Test
    void toDTO_HappyPath() {
        // Arrange
        TestEntity entity = new TestEntity("test");
        TestDTO dto = new TestDTO("test");
        when(modelMapper.map(entity, TestDTO.class)).thenReturn(dto);

        // Act
        TestDTO result = genericMapper.toDTO(entity, TestDTO.class);

        // Assert
        assertNotNull(result);
        assertEquals("test", result.getValue());
        verify(modelMapper).map(entity, TestDTO.class);
    }

    @Test
    void toEntity_HappyPath() {
        // Arrange
        TestDTO dto = new TestDTO("test");
        TestEntity entity = new TestEntity("test");
        when(modelMapper.map(dto, TestEntity.class)).thenReturn(entity);

        // Act
        TestEntity result = genericMapper.toEntity(dto, TestEntity.class);

        // Assert
        assertNotNull(result);
        assertEquals("test", result.getValue());
        verify(modelMapper).map(dto, TestEntity.class);
    }

    @Test
    void toDTO_NullEntity_ReturnsNull() {
        // Arrange
        when(modelMapper.map(null, TestDTO.class)).thenReturn(null);

        // Act
        TestDTO result = genericMapper.toDTO(null, TestDTO.class);

        // Assert
        assertNull(result);
        verify(modelMapper).map(null, TestDTO.class);
    }

    @Test
    void toEntity_NullDTO_ReturnsNull() {
        // Arrange
        when(modelMapper.map(null, TestEntity.class)).thenReturn(null);

        // Act
        TestEntity result = genericMapper.toEntity(null, TestEntity.class);

        // Assert
        assertNull(result);
        verify(modelMapper).map(null, TestEntity.class);
    }

    // Clases auxiliares para el test
    static class TestEntity {
        private String value;
        public TestEntity(String value) { this.value = value; }
        public String getValue() { return value; }
    }

    static class TestDTO {
        private String value;
        public TestDTO(String value) { this.value = value; }
        public String getValue() { return value; }
    }
}