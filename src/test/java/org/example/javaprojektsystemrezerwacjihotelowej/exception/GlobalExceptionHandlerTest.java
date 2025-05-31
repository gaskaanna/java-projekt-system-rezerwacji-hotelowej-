package org.example.javaprojektsystemrezerwacjihotelowej.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleNotFound_ShouldReturnNotFoundStatus() {
        // Arrange
        NoSuchElementException exception = new NoSuchElementException("Resource not found");

        // Act
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleNotFound(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Resource not found", response.getBody().get("error"));
    }

    @Test
    void handleValidation_ShouldReturnBadRequestStatus() {
        // Arrange
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        
        FieldError fieldError1 = new FieldError("object", "field1", "Field 1 error");
        FieldError fieldError2 = new FieldError("object", "field2", "Field 2 error");
        
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        // Act
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleValidation(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Field 1 error", response.getBody().get("field1"));
        assertEquals("Field 2 error", response.getBody().get("field2"));
        assertEquals(2, response.getBody().size());
    }

    @Test
    void handleGeneric_ShouldReturnInternalServerErrorStatus() {
        // Arrange
        Exception exception = new RuntimeException("Some unexpected error");

        // Act
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleGeneric(exception);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Wewnętrzny błąd serwera", response.getBody().get("error"));
    }
}