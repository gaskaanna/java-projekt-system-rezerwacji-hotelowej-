package org.example.javaprojektsystemrezerwacjihotelowej.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenTest {

    private RefreshToken refreshToken;
    private User user;

    @BeforeEach
    void setUp() {
        // Create test user
        user = new User();
        user.setUser_id(1L);
        user.setEmail("test@example.com");
        user.setUsername("testuser");

        // Create test refresh token
        UUID tokenId = UUID.randomUUID();
        refreshToken = RefreshToken.builder()
                .id(tokenId)
                .token("refresh-token-123")
                .user(user)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .build();
    }

    @Test
    void testRefreshTokenCreation() {
        // Assert all fields are set correctly
        assertNotNull(refreshToken.getId());
        assertEquals("refresh-token-123", refreshToken.getToken());
        assertEquals(user, refreshToken.getUser());
        assertNotNull(refreshToken.getExpiryDate());
        assertFalse(refreshToken.isRevoked());
    }

    @Test
    void testCreatedAtField() {
        // We can't directly call prePersist as it's private
        // Instead, we'll set createdAt manually for testing
        LocalDateTime testTime = LocalDateTime.now();
        java.lang.reflect.Field field;
        try {
            field = RefreshToken.class.getDeclaredField("createdAt");
            field.setAccessible(true);
            field.set(refreshToken, testTime);
        } catch (Exception e) {
            fail("Failed to set createdAt field: " + e.getMessage());
        }

        // Assert createdAt is set
        assertEquals(testTime, refreshToken.getCreatedAt());
    }

    @Test
    void testSetAndGetToken() {
        // Test setting and getting token
        refreshToken.setToken("new-refresh-token");
        assertEquals("new-refresh-token", refreshToken.getToken());
    }

    @Test
    void testSetAndGetUser() {
        // Create a new user
        User newUser = new User();
        newUser.setUser_id(2L);
        newUser.setEmail("new@example.com");
        
        // Test setting and getting user
        refreshToken.setUser(newUser);
        assertEquals(newUser, refreshToken.getUser());
    }

    @Test
    void testSetAndGetExpiryDate() {
        // Test setting and getting expiry date
        LocalDateTime newDate = LocalDateTime.now().plusDays(14);
        refreshToken.setExpiryDate(newDate);
        assertEquals(newDate, refreshToken.getExpiryDate());
    }

    @Test
    void testSetAndGetRevoked() {
        // Test setting and getting revoked flag
        assertFalse(refreshToken.isRevoked());
        
        refreshToken.setRevoked(true);
        assertTrue(refreshToken.isRevoked());
        
        refreshToken.setRevoked(false);
        assertFalse(refreshToken.isRevoked());
    }

    @Test
    void testBuilderWithAllFields() {
        // Test builder with all fields
        UUID tokenId = UUID.randomUUID();
        LocalDateTime expiryDate = LocalDateTime.now().plusDays(10);
        LocalDateTime createdAt = LocalDateTime.now();
        
        RefreshToken token = RefreshToken.builder()
                .id(tokenId)
                .token("builder-token")
                .user(user)
                .expiryDate(expiryDate)
                .revoked(true)
                .createdAt(createdAt)
                .build();
        
        assertEquals(tokenId, token.getId());
        assertEquals("builder-token", token.getToken());
        assertEquals(user, token.getUser());
        assertEquals(expiryDate, token.getExpiryDate());
        assertTrue(token.isRevoked());
        assertEquals(createdAt, token.getCreatedAt());
    }
}