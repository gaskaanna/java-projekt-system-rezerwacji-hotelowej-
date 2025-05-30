package org.example.javaprojektsystemrezerwacjihotelowej.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.example.javaprojektsystemrezerwacjihotelowej.properties.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class JwtServiceTest {

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
        Collection<GrantedAuthority> authorities = Arrays.asList(
            new SimpleGrantedAuthority("ROLE_USER"),
            new SimpleGrantedAuthority("ROLE_ADMIN")
        );
        userDetails = new User("test@example.com", "password", authorities);
    }

    @Test
    void generateToken_ShouldCreateValidJwt() {
        // Act
        String token = jwtService.generateToken(userDetails);
        
        // Assert
        assertNotNull(token);
        assertTrue(token.length() > 0);
        
        // Verify token can be parsed
        Claims claims = Jwts.parser()
                .verifyWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(
                        java.util.Base64.getDecoder().decode(SECRET_KEY)))
                .build()
                .parseSignedClaims(token)
                .getPayload();
        
        assertEquals("test@example.com", claims.getSubject());
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        
        // Verify roles are included
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) claims.get("roles");
        assertNotNull(roles);
        assertEquals(2, roles.size());
        assertTrue(roles.contains("ROLE_USER"));
        assertTrue(roles.contains("ROLE_ADMIN"));
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        // Arrange
        String token = jwtService.generateToken(userDetails);
        
        // Act
        String username = jwtService.extractUsername(token);
        
        // Assert
        assertEquals("test@example.com", username);
    }

    @Test
    void isTokenValid_ShouldReturnTrueForValidToken() {
        // Arrange
        String token = jwtService.generateToken(userDetails);
        
        // Act
        boolean isValid = jwtService.isTokenValid(token, userDetails);
        
        // Assert
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_ShouldReturnFalseForExpiredToken() throws Exception {
        // Arrange - Create a token that's already expired
        when(jwtProperties.getExpirationMs()).thenReturn(-10000L); // Negative value to make it expired
        String expiredToken = jwtService.generateToken(userDetails);
        
        // Reset to normal expiration for the validation
        when(jwtProperties.getExpirationMs()).thenReturn(3600000L);
        
        // Act
        boolean isValid = jwtService.isTokenValid(expiredToken, userDetails);
        
        // Assert
        assertFalse(isValid);
    }

    @Test
    void isTokenValid_ShouldReturnFalseForDifferentUser() {
        // Arrange
        String token = jwtService.generateToken(userDetails);
        
        // Create a different user
        UserDetails differentUser = new User(
            "other@example.com", 
            "password", 
            List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        
        // Act
        boolean isValid = jwtService.isTokenValid(token, differentUser);
        
        // Assert
        assertFalse(isValid);
    }
}