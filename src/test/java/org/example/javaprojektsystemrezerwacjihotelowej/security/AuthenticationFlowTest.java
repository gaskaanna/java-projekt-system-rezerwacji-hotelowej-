package org.example.javaprojektsystemrezerwacjihotelowej.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.javaprojektsystemrezerwacjihotelowej.dto.LoginRequest;
import org.example.javaprojektsystemrezerwacjihotelowej.dto.RegistrationRequest;
import org.example.javaprojektsystemrezerwacjihotelowej.dto.TokenPairResponse;
import org.example.javaprojektsystemrezerwacjihotelowej.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockCookie;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for the authentication flow.
 * These tests verify that the authentication process is working correctly.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    public void login_WithValidCredentials_ShouldReturnTokens() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password123");
        TokenPairResponse tokenPair = new TokenPairResponse("access.token.123", "refresh.token.456");

        when(authService.login(any(LoginRequest.class))).thenReturn(tokenPair);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("accessToken"))
                .andExpect(cookie().exists("refreshToken"))
                .andExpect(jsonPath("$.message").value("Zalogowano pomyślnie"));
    }

    @Test
    public void register_WithValidData_ShouldReturnTokens() throws Exception {
        // Arrange
        RegistrationRequest registrationRequest = new RegistrationRequest("test@example.com", "password123");
        TokenPairResponse tokenPair = new TokenPairResponse("access.token.123", "refresh.token.456");

        when(authService.register(any(RegistrationRequest.class))).thenReturn(tokenPair);

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("accessToken"))
                .andExpect(cookie().exists("refreshToken"))
                .andExpect(jsonPath("$.message").value("Zarejestrowano pomyślnie"));
    }

    @Test
    public void refresh_WithValidToken_ShouldReturnNewTokens() throws Exception {
        // Arrange
        TokenPairResponse tokenPair = new TokenPairResponse("new.access.token", "new.refresh.token");

        when(authService.refresh(any(String.class))).thenReturn(tokenPair);

        // Act & Assert
        mockMvc.perform(post("/auth/refresh")
                .cookie(new MockCookie("refreshToken", "old.refresh.token")))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("accessToken"))
                .andExpect(cookie().exists("refreshToken"))
                .andExpect(jsonPath("$.message").value("Odświeżono tokeny"));
    }

    @Test
    public void logout_ShouldClearCookies() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(cookie().maxAge("accessToken", 0))
                .andExpect(cookie().maxAge("refreshToken", 0))
                .andExpect(jsonPath("$.message").value("Wylogowano"));
    }
}
