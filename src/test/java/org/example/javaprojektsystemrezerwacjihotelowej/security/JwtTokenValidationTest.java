package org.example.javaprojektsystemrezerwacjihotelowej.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for JWT token validation.
 * These tests verify that JWT tokens can be created and validated correctly.
 */
public class JwtTokenValidationTest {

    // This is a test secret key - never use this in production
    private static final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    private SecretKey key;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        // Initialize the signing key
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        key = Keys.hmacShaKeyFor(keyBytes);

        // Create test user details
        userDetails = new User(
                "test@example.com",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Test
    void generateToken_ShouldCreateValidToken() {
        // Arrange
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "USER");

        // Act
        String token = generateToken(claims, userDetails.getUsername());

        // Assert
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        // Arrange
        String token = generateToken(new HashMap<>(), userDetails.getUsername());

        // Act
        String username = extractUsername(token);

        // Assert
        assertEquals(userDetails.getUsername(), username);
    }

    @Test
    void extractExpiration_ShouldReturnFutureDate() {
        // Arrange
        String token = generateToken(new HashMap<>(), userDetails.getUsername());

        // Act
        Date expiration = extractExpiration(token);

        // Assert
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void isTokenValid_WithValidToken_ShouldReturnTrue() {
        // Arrange
        String token = generateToken(new HashMap<>(), userDetails.getUsername());

        // Act
        boolean isValid = isTokenValid(token, userDetails);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_WithExpiredToken_ShouldReturnFalse() {
        // Arrange
        String token = generateExpiredToken(new HashMap<>(), userDetails.getUsername());

        // Act
        boolean isValid = isTokenValid(token, userDetails);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void isTokenValid_WithWrongUsername_ShouldReturnFalse() {
        // Arrange
        String token = generateToken(new HashMap<>(), "wrong@example.com");

        // Act
        boolean isValid = isTokenValid(token, userDetails);

        // Assert
        assertFalse(isValid);
    }

    // Helper methods that mimic the JwtService implementation

    private String generateToken(Map<String, Object> extraClaims, String subject) {
        Date issued = new Date(System.currentTimeMillis());
        Date expire = new Date(System.currentTimeMillis() + 1000 * 60 * 15); // 15 minutes

        return Jwts.builder()
                .claims(extraClaims)
                .subject(subject)
                .issuedAt(issued)
                .expiration(expire)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    private String generateExpiredToken(Map<String, Object> extraClaims, String subject) {
        Date issued = new Date(System.currentTimeMillis() - 1000 * 60 * 30); // 30 minutes ago
        Date expire = new Date(System.currentTimeMillis() - 1000 * 60 * 15); // 15 minutes ago

        return Jwts.builder()
                .claims(extraClaims)
                .subject(subject)
                .issuedAt(issued)
                .expiration(expire)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    private boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
        } catch (ExpiredJwtException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
