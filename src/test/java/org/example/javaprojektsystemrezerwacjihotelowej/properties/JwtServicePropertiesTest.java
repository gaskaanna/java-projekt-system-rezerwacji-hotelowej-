package org.example.javaprojektsystemrezerwacjihotelowej.properties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.example.javaprojektsystemrezerwacjihotelowej.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the JwtService class to verify it correctly uses JwtProperties.
 * These tests verify that the JWT tokens are generated with the correct expiration time
 * and are signed with the correct secret key.
 */
@SpringBootTest
public class JwtServicePropertiesTest {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JwtProperties jwtProperties;

    @Test
    public void generateToken_ShouldUseCorrectExpirationTime() {
        // Arrange
        UserDetails userDetails = createTestUser();
        
        // Act
        String token = jwtService.generateToken(userDetails);
        
        // Assert
        Claims claims = extractClaims(token);
        Date expirationDate = claims.getExpiration();
        Date issuedAt = claims.getIssuedAt();
        
        // Calculate the difference in milliseconds
        long expirationTimeMs = expirationDate.getTime() - issuedAt.getTime();
        
        // Allow for a small difference due to processing time
        long allowedDifference = 1000; // 1 second
        
        assertTrue(Math.abs(expirationTimeMs - jwtProperties.getExpirationMs()) < allowedDifference,
                "Token expiration time should match the value in JwtProperties");
    }

    @Test
    public void generateToken_ShouldUseCorrectSecret() {
        // Arrange
        UserDetails userDetails = createTestUser();
        
        // Act
        String token = jwtService.generateToken(userDetails);
        
        // Assert
        // If the token can be verified with the secret from JwtProperties, it means
        // the correct secret was used to sign it
        assertDoesNotThrow(() -> {
            Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecret())))
                .build()
                .parseSignedClaims(token);
        }, "Token should be verifiable with the secret from JwtProperties");
    }

    @Test
    public void isTokenValid_ShouldReturnTrueForValidToken() {
        // Arrange
        UserDetails userDetails = createTestUser();
        String token = jwtService.generateToken(userDetails);
        
        // Act
        boolean isValid = jwtService.isTokenValid(token, userDetails);
        
        // Assert
        assertTrue(isValid, "Token should be valid for the user it was generated for");
    }

    private UserDetails createTestUser() {
        return new User(
                "test@example.com",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecret())))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}