package org.example.javaprojektsystemrezerwacjihotelowej.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SecurityConfigTest {

    @Autowired
    private SecurityConfig securityConfig;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Test
    void passwordEncoder_ShouldEncodeAndVerifyPassword() {
        // Arrange
        String rawPassword = "testPassword123";
        
        // Act
        String encodedPassword = passwordEncoder.encode(rawPassword);
        
        // Assert
        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
        assertFalse(passwordEncoder.matches("wrongPassword", encodedPassword));
    }

    @Test
    void userDetailsService_ShouldBeConfigured() {
        // Assert
        assertNotNull(userDetailsService);
    }

    @Test
    void authenticationManager_ShouldBeConfigured() {
        // Assert
        assertNotNull(authenticationManager);
    }

    @Test
    void securityFilterChain_ShouldBeConfigured() {
        // Assert
        assertNotNull(securityConfig);
    }
}