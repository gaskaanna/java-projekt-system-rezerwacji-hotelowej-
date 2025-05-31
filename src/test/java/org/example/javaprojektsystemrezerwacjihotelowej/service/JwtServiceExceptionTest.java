package org.example.javaprojektsystemrezerwacjihotelowej.service;

import org.example.javaprojektsystemrezerwacjihotelowej.properties.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class JwtServiceExceptionTest {

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private JwtService jwtService;

    private UserDetails userDetails;
    private final String SECRET_KEY = "YTJjZDhkNTIzZTA1M2E1NTY0NGU0MDA0YzY1NjJkNjk="; // Base64 encoded test key

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Mock JWT properties
        when(jwtProperties.getSecret()).thenReturn(SECRET_KEY);
        when(jwtProperties.getExpirationMs()).thenReturn(3600000L); // 1 hour
        
        // Create test user details
        userDetails = new User(
            "test@example.com", 
            "password", 
            List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Test
    void isTokenValid_ShouldReturnFalseForInvalidToken() {
        // Arrange - Create an invalid token
        String invalidToken = "invalid.token.format";
        
        // Act
        boolean isValid = jwtService.isTokenValid(invalidToken, userDetails);
        
        // Assert
        assertFalse(isValid);
    }
    
    @Test
    void isTokenValid_ShouldReturnFalseForNullToken() {
        // Act
        boolean isValid = jwtService.isTokenValid(null, userDetails);
        
        // Assert
        assertFalse(isValid);
    }
    
    @Test
    void isTokenValid_ShouldReturnFalseForEmptyToken() {
        // Act
        boolean isValid = jwtService.isTokenValid("", userDetails);
        
        // Assert
        assertFalse(isValid);
    }
}