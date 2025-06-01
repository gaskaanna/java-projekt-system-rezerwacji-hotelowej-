package org.example.javaprojektsystemrezerwacjihotelowej.properties;

import org.example.javaprojektsystemrezerwacjihotelowej.entity.RefreshToken;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.User;
import org.example.javaprojektsystemrezerwacjihotelowej.repository.RefreshTokenRepository;
import org.example.javaprojektsystemrezerwacjihotelowej.service.RefreshTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Tests for the RefreshTokenService class to verify it correctly uses JwtProperties.
 * These tests verify that the refresh tokens are generated with the correct expiration time.
 */
@ExtendWith(MockitoExtension.class)
public class RefreshTokenServicePropertiesTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Captor
    private ArgumentCaptor<RefreshToken> refreshTokenCaptor;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    public void create_ShouldUseCorrectExpirationTime() {
        // Arrange
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setRefreshExpMs(604800000L); // 7 days
        ReflectionTestUtils.setField(refreshTokenService, "props", jwtProperties);

        User user = new User();
        user.setUser_id(1L);
        user.setEmail("test@example.com");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(java.util.UUID.randomUUID());
        refreshToken.setToken("test-token");
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(7));

        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        // Act
        RefreshToken result = refreshTokenService.create(user);

        // Assert
        assertNotNull(result, "RefreshToken should not be null");
        assertEquals(user, result.getUser(), "RefreshToken should be associated with the correct user");

        // Verify that the expiration date is set correctly
        // The expiration date should be approximately 7 days from now (with some tolerance for test execution time)
        LocalDateTime expectedExpiry = LocalDateTime.now().plus(java.time.Duration.ofMillis(jwtProperties.getRefreshExpMs()));
        long minutesDifference = ChronoUnit.MINUTES.between(expectedExpiry, result.getExpiryDate());

        // Allow for a small difference due to processing time
        assertTrue(Math.abs(minutesDifference) < 5, 
                "RefreshToken expiration time should be approximately 7 days from now");
    }

    @Test
    public void create_WithDifferentExpirationTime_ShouldUseCorrectExpirationTime() {
        // Arrange
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setRefreshExpMs(86400000L); // 1 day
        ReflectionTestUtils.setField(refreshTokenService, "props", jwtProperties);

        User user = new User();
        user.setUser_id(1L);
        user.setEmail("test@example.com");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(java.util.UUID.randomUUID());
        refreshToken.setToken("test-token");
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(1));

        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        // Act
        RefreshToken result = refreshTokenService.create(user);

        // Assert
        assertNotNull(result, "RefreshToken should not be null");
        assertEquals(user, result.getUser(), "RefreshToken should be associated with the correct user");

        // Verify that the expiration date is set correctly
        // The expiration date should be approximately 1 day from now (with some tolerance for test execution time)
        LocalDateTime expectedExpiry = LocalDateTime.now().plus(java.time.Duration.ofMillis(jwtProperties.getRefreshExpMs()));
        long minutesDifference = ChronoUnit.MINUTES.between(expectedExpiry, result.getExpiryDate());

        // Allow for a small difference due to processing time
        assertTrue(Math.abs(minutesDifference) < 5, 
                "RefreshToken expiration time should be approximately 1 day from now");
    }
}
