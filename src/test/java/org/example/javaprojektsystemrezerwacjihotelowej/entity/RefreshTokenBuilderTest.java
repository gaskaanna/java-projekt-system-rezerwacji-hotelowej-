package org.example.javaprojektsystemrezerwacjihotelowej.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the RefreshToken.RefreshTokenBuilder class.
 * These tests verify that the builder correctly creates RefreshToken objects with all fields.
 */
class RefreshTokenBuilderTest {

    @Test
    void testRefreshTokenBuilder_WithAllFields() {
        // Arrange
        UUID id = UUID.randomUUID();
        String token = "test-token";
        User user = new User();
        user.setUser_id(1L);
        user.setEmail("test@example.com");
        LocalDateTime expiryDate = LocalDateTime.now().plusDays(7);
        boolean revoked = false;
        LocalDateTime createdAt = LocalDateTime.now();

        // Act
        RefreshToken refreshToken = RefreshToken.builder()
                .id(id)
                .token(token)
                .user(user)
                .expiryDate(expiryDate)
                .revoked(revoked)
                .createdAt(createdAt)
                .build();

        // Assert
        assertEquals(id, refreshToken.getId());
        assertEquals(token, refreshToken.getToken());
        assertEquals(user, refreshToken.getUser());
        assertEquals(expiryDate, refreshToken.getExpiryDate());
        assertEquals(revoked, refreshToken.isRevoked());
        assertEquals(createdAt, refreshToken.getCreatedAt());
    }

    @Test
    void testRefreshTokenBuilder_WithRequiredFieldsOnly() {
        // Arrange
        String token = "test-token";
        User user = new User();
        user.setUser_id(1L);
        user.setEmail("test@example.com");
        LocalDateTime expiryDate = LocalDateTime.now().plusDays(7);

        // Act
        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .user(user)
                .expiryDate(expiryDate)
                .build();

        // Assert
        assertNull(refreshToken.getId());
        assertEquals(token, refreshToken.getToken());
        assertEquals(user, refreshToken.getUser());
        assertEquals(expiryDate, refreshToken.getExpiryDate());
        assertFalse(refreshToken.isRevoked()); // Default value
        assertNull(refreshToken.getCreatedAt());
    }

    @Test
    void testRefreshTokenBuilder_WithDefaultRevoked() {
        // Arrange
        String token = "test-token";
        User user = new User();
        user.setUser_id(1L);
        user.setEmail("test@example.com");
        LocalDateTime expiryDate = LocalDateTime.now().plusDays(7);

        // Act
        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .user(user)
                .expiryDate(expiryDate)
                .build();

        // Assert
        assertFalse(refreshToken.isRevoked()); // Default value should be false
    }

    @Test
    void testRefreshTokenBuilder_WithExplicitRevoked() {
        // Arrange
        String token = "test-token";
        User user = new User();
        user.setUser_id(1L);
        user.setEmail("test@example.com");
        LocalDateTime expiryDate = LocalDateTime.now().plusDays(7);

        // Act
        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .user(user)
                .expiryDate(expiryDate)
                .revoked(true)
                .build();

        // Assert
        assertTrue(refreshToken.isRevoked());
    }

    @Test
    void testRefreshTokenBuilder_WithNoFields() {
        // Act
        RefreshToken refreshToken = RefreshToken.builder().build();

        // Assert
        assertNull(refreshToken.getId());
        assertNull(refreshToken.getToken());
        assertNull(refreshToken.getUser());
        assertNull(refreshToken.getExpiryDate());
        assertFalse(refreshToken.isRevoked()); // Default value
        assertNull(refreshToken.getCreatedAt());
    }

    @Test
    void testRefreshTokenBuilder_ToString() {
        // Arrange
        UUID id = UUID.randomUUID();
        String token = "test-token";
        User user = new User();
        user.setUser_id(1L);
        user.setEmail("test@example.com");
        LocalDateTime expiryDate = LocalDateTime.now().plusDays(7);
        boolean revoked = true;
        LocalDateTime createdAt = LocalDateTime.now();

        // Act
        RefreshToken.RefreshTokenBuilder builder = RefreshToken.builder()
                .id(id)
                .token(token)
                .user(user)
                .expiryDate(expiryDate)
                .revoked(revoked)
                .createdAt(createdAt);

        // Assert
        String builderString = builder.toString();
        System.out.println("[DEBUG_LOG] Builder toString: " + builderString);

        // Check that the toString contains the field names
        assertTrue(builderString.contains("RefreshTokenBuilder"));
        assertTrue(builderString.contains("id"));
        assertTrue(builderString.contains("token"));
        assertTrue(builderString.contains("user"));
        assertTrue(builderString.contains("expiryDate"));
        assertTrue(builderString.contains("revoked"));
        assertTrue(builderString.contains("createdAt"));

        // Check that the toString contains the field values or their string representations
        assertTrue(builderString.contains(id.toString()));
        assertTrue(builderString.contains(token));
        assertTrue(builderString.contains(String.valueOf(revoked)));
    }
}
