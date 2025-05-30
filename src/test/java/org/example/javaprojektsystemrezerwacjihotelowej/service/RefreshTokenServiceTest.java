package org.example.javaprojektsystemrezerwacjihotelowej.service;

import org.example.javaprojektsystemrezerwacjihotelowej.entity.RefreshToken;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.User;
import org.example.javaprojektsystemrezerwacjihotelowej.properties.JwtProperties;
import org.example.javaprojektsystemrezerwacjihotelowej.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private User testUser;
    private RefreshToken testToken;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Mock JWT properties
        when(jwtProperties.getRefreshExpMs()).thenReturn(604800000L); // 7 days
        
        // Create test user
        testUser = new User();
        testUser.setUser_id(1L);
        testUser.setEmail("test@example.com");
        
        // Create test refresh token
        testToken = new RefreshToken();
        testToken.setId(UUID.randomUUID());
        testToken.setToken("test-refresh-token");
        testToken.setUser(testUser);
        testToken.setExpiryDate(LocalDateTime.now().plusDays(7));
        testToken.setRevoked(false);
        testToken.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void create_ShouldGenerateAndSaveRefreshToken() {
        // Arrange
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        RefreshToken result = refreshTokenService.create(testUser);
        
        // Assert
        assertNotNull(result);
        assertNotNull(result.getToken());
        assertEquals(testUser, result.getUser());
        assertFalse(result.isRevoked());
        assertNotNull(result.getExpiryDate());
        
        // Verify repository was called
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
        
        // Capture the saved token to verify its properties
        ArgumentCaptor<RefreshToken> tokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(tokenCaptor.capture());
        RefreshToken capturedToken = tokenCaptor.getValue();
        
        assertEquals(testUser, capturedToken.getUser());
        assertFalse(capturedToken.isRevoked());
        assertTrue(capturedToken.getExpiryDate().isAfter(LocalDateTime.now()));
    }

    @Test
    void verify_ShouldReturnTokenWhenValid() {
        // Arrange
        when(refreshTokenRepository.findByToken("test-refresh-token")).thenReturn(Optional.of(testToken));
        
        // Act
        RefreshToken result = refreshTokenService.verify("test-refresh-token");
        
        // Assert
        assertNotNull(result);
        assertEquals(testToken, result);
        
        // Verify repository was called
        verify(refreshTokenRepository, times(1)).findByToken("test-refresh-token");
    }

    @Test
    void verify_ShouldThrowExceptionWhenTokenNotFound() {
        // Arrange
        when(refreshTokenRepository.findByToken("non-existent-token")).thenReturn(Optional.empty());
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> refreshTokenService.verify("non-existent-token")
        );
        
        assertEquals("Invalid refresh token", exception.getMessage());
        
        // Verify repository was called
        verify(refreshTokenRepository, times(1)).findByToken("non-existent-token");
    }

    @Test
    void verify_ShouldThrowExceptionWhenTokenRevoked() {
        // Arrange
        testToken.setRevoked(true);
        when(refreshTokenRepository.findByToken("revoked-token")).thenReturn(Optional.of(testToken));
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> refreshTokenService.verify("revoked-token")
        );
        
        assertEquals("Refresh token expired or revoked", exception.getMessage());
        
        // Verify repository was called
        verify(refreshTokenRepository, times(1)).findByToken("revoked-token");
    }

    @Test
    void verify_ShouldThrowExceptionWhenTokenExpired() {
        // Arrange
        testToken.setExpiryDate(LocalDateTime.now().minusDays(1)); // Expired yesterday
        when(refreshTokenRepository.findByToken("expired-token")).thenReturn(Optional.of(testToken));
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> refreshTokenService.verify("expired-token")
        );
        
        assertEquals("Refresh token expired or revoked", exception.getMessage());
        
        // Verify repository was called
        verify(refreshTokenRepository, times(1)).findByToken("expired-token");
    }

    @Test
    void rotate_ShouldRevokeOldTokenAndCreateNew() {
        // Arrange
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        RefreshToken result = refreshTokenService.rotate(testToken);
        
        // Assert
        // Verify old token is revoked
        assertTrue(testToken.isRevoked());
        
        // Verify new token is created
        assertNotNull(result);
        assertNotEquals(testToken.getToken(), result.getToken());
        assertEquals(testUser, result.getUser());
        assertFalse(result.isRevoked());
        
        // Verify repository was called twice (once to save revoked token, once to save new token)
        verify(refreshTokenRepository, times(2)).save(any(RefreshToken.class));
    }
}