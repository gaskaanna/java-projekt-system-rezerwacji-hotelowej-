package org.example.javaprojektsystemrezerwacjihotelowej.controller;

import org.example.javaprojektsystemrezerwacjihotelowej.dto.*;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.RoleName;
import org.example.javaprojektsystemrezerwacjihotelowej.properties.JwtProperties;
import org.example.javaprojektsystemrezerwacjihotelowej.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private AuthController authController;

    private LoginRequest loginRequest;
    private RegistrationRequest registrationRequest;
    private TokenPairResponse tokenPairResponse;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        // Setup JWT properties
        when(jwtProperties.getExpirationMs()).thenReturn(900000L); // 15 minutes
        when(jwtProperties.getRefreshExpMs()).thenReturn(86400000L); // 24 hours

        // Create test login request
        loginRequest = new LoginRequest("test@example.com", "password123");

        // Create test registration request
        registrationRequest = new RegistrationRequest(
                "test@example.com",
                "password123"
        );

        // Create test token pair response
        tokenPairResponse = new TokenPairResponse(
                "test.access.token",
                "test.refresh.token"
        );

        // Setup mock responses
        when(authService.login(any(LoginRequest.class))).thenReturn(tokenPairResponse);
        when(authService.register(any(RegistrationRequest.class))).thenReturn(tokenPairResponse);
        when(authService.refresh(anyString())).thenReturn(tokenPairResponse);

        // Create mock user details
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        userDetails = new User("test@example.com", "password", authorities);
    }

    @Test
    void login_ShouldReturnTokensInCookies() {
        // Act
        ResponseEntity<ApiMessageResponse> response = authController.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Zalogowano pomyślnie", response.getBody().message());

        // Verify cookies are set
        assertTrue(response.getHeaders().containsKey(HttpHeaders.SET_COOKIE));

        // Verify service was called
        verify(authService, times(1)).login(loginRequest);
    }

    @Test
    void register_ShouldReturnTokensInCookies() {
        // Act
        ResponseEntity<ApiMessageResponse> response = authController.register(registrationRequest);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Zarejestrowano pomyślnie", response.getBody().message());

        // Verify cookies are set
        assertTrue(response.getHeaders().containsKey(HttpHeaders.SET_COOKIE));

        // Verify service was called
        verify(authService, times(1)).register(registrationRequest);
    }

    @Test
    void refresh_ShouldReturnNewTokensInCookies() {
        // Arrange
        String refreshToken = "test.refresh.token";

        // Act
        ResponseEntity<ApiMessageResponse> response = authController.refresh(refreshToken);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Odświeżono tokeny", response.getBody().message());

        // Verify cookies are set
        assertTrue(response.getHeaders().containsKey(HttpHeaders.SET_COOKIE));

        // Verify service was called
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

        // Verify cookies are set with max-age=0
        assertTrue(response.getHeaders().containsKey(HttpHeaders.SET_COOKIE));
        assertTrue(response.getHeaders().get(HttpHeaders.SET_COOKIE).toString().contains("Max-Age=0"));
    }

    @Test
    void currentUser_WhenAuthenticated_ShouldReturnUserInfo() {
        // Act
        ResponseEntity<?> response = authController.currentUser(userDetails);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof UserInfoResponse);

        UserInfoResponse userInfo = (UserInfoResponse) response.getBody();
        assertEquals("test@example.com", userInfo.username());
        assertEquals("test@example.com", userInfo.email());
        assertTrue(userInfo.roles().contains("ROLE_USER"));
        assertEquals("Zalogowany", userInfo.comment());
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
