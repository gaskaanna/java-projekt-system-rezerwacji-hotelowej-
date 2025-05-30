package org.example.javaprojektsystemrezerwacjihotelowej.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.RefreshToken;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.User;
import org.example.javaprojektsystemrezerwacjihotelowej.properties.JwtProperties;
import org.example.javaprojektsystemrezerwacjihotelowej.service.JwtService;
import org.example.javaprojektsystemrezerwacjihotelowej.service.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private UserDetails userDetails;
    private User user;
    private RefreshToken refreshToken;
    private String validToken;
    private String validRefreshToken;

    @BeforeEach
    void setUp() {
        // Clear security context before each test
        SecurityContextHolder.clearContext();

        // Create test user
        user = new User();
        user.setUser_id(1L);
        user.setEmail("test@example.com");
        user.setUsername("testuser");
        user.setPassword("password");

        // Create user details
        userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("test@example.com")
                .password("password")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        // Create refresh token
        refreshToken = RefreshToken.builder()
                .id(UUID.randomUUID())
                .token("refresh-token-123")
                .user(user)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .build();

        // Set up tokens
        validToken = "valid-jwt-token";
        validRefreshToken = "refresh-token-123";

        // Configure JWT service mock
        when(jwtService.extractUsername(validToken)).thenReturn("test@example.com");
        when(jwtService.isTokenValid(validToken, userDetails)).thenReturn(true);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("new-jwt-token");

        // Configure UserDetailsService mock
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);

        // Configure RefreshTokenService mock
        when(refreshTokenService.verify(validRefreshToken)).thenReturn(refreshToken);
        when(refreshTokenService.rotate(any(RefreshToken.class))).thenReturn(refreshToken);

        // Configure JwtProperties mock
        when(jwtProperties.getExpirationMs()).thenReturn(900000L);
        when(jwtProperties.getRefreshExpMs()).thenReturn(604800000L);
    }

    @Test
    void doFilterInternal_WithValidJwtInHeader_ShouldAuthenticate() throws ServletException, IOException {
        // Arrange
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + validToken);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("test@example.com", SecurityContextHolder.getContext().getAuthentication().getName());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithValidJwtInCookie_ShouldAuthenticate() throws ServletException, IOException {
        // Arrange
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);
        Cookie[] cookies = new Cookie[] { new Cookie("accessToken", validToken) };
        when(request.getCookies()).thenReturn(cookies);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("test@example.com", SecurityContextHolder.getContext().getAuthentication().getName());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithInvalidJwtButValidRefreshToken_ShouldRefreshAndAuthenticate() throws ServletException, IOException {
        // Arrange
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);
        Cookie[] cookies = new Cookie[] { 
            new Cookie("accessToken", "invalid-token"),
            new Cookie("refreshToken", validRefreshToken) 
        };
        when(request.getCookies()).thenReturn(cookies);
        when(jwtService.extractUsername("invalid-token")).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("test@example.com", SecurityContextHolder.getContext().getAuthentication().getName());
        verify(refreshTokenService).verify(validRefreshToken);
        verify(refreshTokenService).rotate(any(RefreshToken.class));
        verify(response, times(2)).addHeader(eq(HttpHeaders.SET_COOKIE), anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithNoTokens_ShouldNotAuthenticate() throws ServletException, IOException {
        // Arrange
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);
        when(request.getCookies()).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithExpiredJwtAndNoRefreshToken_ShouldNotAuthenticate() throws ServletException, IOException {
        // Arrange
        String expiredToken = "expired-token";
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + expiredToken);
        when(jwtService.extractUsername(expiredToken)).thenReturn("test@example.com");
        when(jwtService.isTokenValid(expiredToken, userDetails)).thenReturn(false);
        when(request.getCookies()).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithInvalidRefreshToken_ShouldNotAuthenticate() throws ServletException, IOException {
        // Arrange
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);
        Cookie[] cookies = new Cookie[] { new Cookie("refreshToken", "invalid-refresh-token") };
        when(request.getCookies()).thenReturn(cookies);
        when(refreshTokenService.verify("invalid-refresh-token")).thenThrow(new IllegalArgumentException("Invalid refresh token"));

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
}