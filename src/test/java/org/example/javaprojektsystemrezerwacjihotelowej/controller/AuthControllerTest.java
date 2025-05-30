package org.example.javaprojektsystemrezerwacjihotelowej.controller;

import org.example.javaprojektsystemrezerwacjihotelowej.dto.ApiMessageResponse;
import org.example.javaprojektsystemrezerwacjihotelowej.dto.LoginRequest;
import org.example.javaprojektsystemrezerwacjihotelowej.dto.RegistrationRequest;
import org.example.javaprojektsystemrezerwacjihotelowej.dto.TokenPairResponse;
import org.example.javaprojektsystemrezerwacjihotelowej.properties.JwtProperties;
import org.example.javaprojektsystemrezerwacjihotelowej.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(jwtProperties.getExpirationMs()).thenReturn(3600000L);
        when(jwtProperties.getRefreshExpMs()).thenReturn(86400000L);
    }

    @Test
    void register_ShouldReturnTokenPairAndSetCookies() {
        // Arrange
        RegistrationRequest request = new RegistrationRequest("test@example.com", "password123");
        TokenPairResponse tokenPair = new TokenPairResponse("access-token", "refresh-token");
        when(authService.register(any(RegistrationRequest.class))).thenReturn(tokenPair);

        // Act
        ResponseEntity<ApiMessageResponse> response = authController.register(request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Zarejestrowano pomyślnie", response.getBody().message());
        assertTrue(response.getHeaders().containsKey(HttpHeaders.SET_COOKIE));
        verify(authService, times(1)).register(request);
    }

    @Test
    void login_ShouldReturnTokenPairAndSetCookies() {
        // Arrange
        LoginRequest request = new LoginRequest("test@example.com", "password123");
        TokenPairResponse tokenPair = new TokenPairResponse("access-token", "refresh-token");
        when(authService.login(any(LoginRequest.class))).thenReturn(tokenPair);

        // Act
        ResponseEntity<ApiMessageResponse> response = authController.login(request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Zalogowano pomyślnie", response.getBody().message());
        assertTrue(response.getHeaders().containsKey(HttpHeaders.SET_COOKIE));
        verify(authService, times(1)).login(request);
    }

    @Test
    void refresh_ShouldReturnNewTokenPairAndSetCookies() {
        // Arrange
        String refreshToken = "old-refresh-token";
        TokenPairResponse tokenPair = new TokenPairResponse("new-access-token", "new-refresh-token");
        when(authService.refresh(refreshToken)).thenReturn(tokenPair);

        // Act
        ResponseEntity<ApiMessageResponse> response = authController.refresh(refreshToken);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Odświeżono tokeny", response.getBody().message());
        assertTrue(response.getHeaders().containsKey(HttpHeaders.SET_COOKIE));
        verify(authService, times(1)).refresh(refreshToken);
    }

    @Test
    void logout_ShouldClearCookies() {
        // Act
        ResponseEntity<ApiMessageResponse> response = authController.logout();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Wylogowano", response.getBody().message());
        
        // Verify cookies are cleared (maxAge=0)
        assertTrue(response.getHeaders().containsKey(HttpHeaders.SET_COOKIE));
        String cookies = response.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        assertTrue(cookies.contains("Max-Age=0"));
    }

    @Test
    void currentUser_WhenAuthenticated_ShouldReturnUserInfo() {
        // Arrange
        Collection<GrantedAuthority> authorities = 
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetails userDetails = new User("user@example.com", "password", authorities);

        // Act
        ResponseEntity<?> response = authController.currentUser(userDetails);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        verify(authService, never()).login(any());
    }

    @Test
    void currentUser_WhenNotAuthenticated_ShouldReturnMessage() {
        // Act
        ResponseEntity<?> response = authController.currentUser(null);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Nie jesteś zalogowany", response.getBody());
    }
}