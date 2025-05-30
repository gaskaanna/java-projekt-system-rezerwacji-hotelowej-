package org.example.javaprojektsystemrezerwacjihotelowej.service;

import org.example.javaprojektsystemrezerwacjihotelowej.dto.LoginRequest;
import org.example.javaprojektsystemrezerwacjihotelowej.dto.RegistrationRequest;
import org.example.javaprojektsystemrezerwacjihotelowej.dto.TokenPairResponse;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.RefreshToken;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.Role;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.RoleName;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.User;
import org.example.javaprojektsystemrezerwacjihotelowej.repository.RoleRepository;
import org.example.javaprojektsystemrezerwacjihotelowej.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthService authService;

    private RegistrationRequest registrationRequest;
    private LoginRequest loginRequest;
    private User testUser;
    private Role userRole;
    private UserDetails userDetails;
    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Setup test data
        registrationRequest = new RegistrationRequest("test@example.com", "password123");
        loginRequest = new LoginRequest("test@example.com", "password123");
        
        userRole = new Role();
        userRole.setId(1);
        userRole.setName(RoleName.USER);
        
        testUser = new User();
        testUser.setUser_id(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword("encoded_password");
        testUser.setRoles(Set.of(userRole));
        
        userDetails = org.springframework.security.core.userdetails.User
                .withUsername("test@example.com")
                .password("encoded_password")
                .authorities("ROLE_USER")
                .build();
        
        refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-token-value");
        refreshToken.setUser(testUser);
        
        // Setup mocks
        when(roleRepository.findByName(RoleName.USER)).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwt-token-value");
        when(refreshTokenService.create(any(User.class))).thenReturn(refreshToken);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
    }

    @Test
    void register_ShouldCreateUserAndReturnTokens() {
        // Arrange
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        
        // Act
        TokenPairResponse response = authService.register(registrationRequest);
        
        // Assert
        assertNotNull(response);
        assertEquals("jwt-token-value", response.accessToken());
        assertEquals("refresh-token-value", response.refreshToken());
        
        // Verify interactions
        verify(userRepository).existsByEmail("test@example.com");
        verify(roleRepository).findByName(RoleName.USER);
        verify(passwordEncoder).encode("password123");
        
        // Capture the user being saved to verify its properties
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        
        assertEquals("test@example.com", savedUser.getEmail());
        assertEquals("encoded_password", savedUser.getPassword());
        assertTrue(savedUser.getRoles().contains(userRole));
        
        verify(userDetailsService).loadUserByUsername("test@example.com");
        verify(jwtService).generateToken(userDetails);
        verify(refreshTokenService).create(testUser);
    }

    @Test
    void register_ShouldThrowExceptionWhenEmailExists() {
        // Arrange
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> authService.register(registrationRequest)
        );
        
        assertEquals("E-mail already used", exception.getMessage());
        
        // Verify interactions
        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_ShouldThrowExceptionWhenRoleNotFound() {
        // Arrange
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(roleRepository.findByName(RoleName.USER)).thenReturn(Optional.empty());
        
        // Act & Assert
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> authService.register(registrationRequest)
        );
        
        assertEquals("ROLE_USER missing", exception.getMessage());
        
        // Verify interactions
        verify(userRepository).existsByEmail("test@example.com");
        verify(roleRepository).findByName(RoleName.USER);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_ShouldAuthenticateAndReturnTokens() {
        // Act
        TokenPairResponse response = authService.login(loginRequest);
        
        // Assert
        assertNotNull(response);
        assertEquals("jwt-token-value", response.accessToken());
        assertEquals("refresh-token-value", response.refreshToken());
        
        // Verify interactions
        verify(authManager).authenticate(
            new UsernamePasswordAuthenticationToken("test@example.com", "password123")
        );
        verify(userRepository).findByEmail("test@example.com");
        verify(userDetailsService).loadUserByUsername("test@example.com");
        verify(jwtService).generateToken(userDetails);
        verify(refreshTokenService).create(testUser);
    }

    @Test
    void refresh_ShouldVerifyRotateAndReturnNewTokens() {
        // Arrange
        RefreshToken rotatedToken = new RefreshToken();
        rotatedToken.setToken("new-refresh-token");
        rotatedToken.setUser(testUser);
        
        when(refreshTokenService.verify("old-refresh-token")).thenReturn(refreshToken);
        when(refreshTokenService.rotate(refreshToken)).thenReturn(rotatedToken);
        
        // Act
        TokenPairResponse response = authService.refresh("old-refresh-token");
        
        // Assert
        assertNotNull(response);
        assertEquals("jwt-token-value", response.accessToken());
        assertEquals("new-refresh-token", response.refreshToken());
        
        // Verify interactions
        verify(refreshTokenService).verify("old-refresh-token");
        verify(refreshTokenService).rotate(refreshToken);
        verify(userDetailsService).loadUserByUsername("test@example.com");
        verify(jwtService).generateToken(userDetails);
    }
}