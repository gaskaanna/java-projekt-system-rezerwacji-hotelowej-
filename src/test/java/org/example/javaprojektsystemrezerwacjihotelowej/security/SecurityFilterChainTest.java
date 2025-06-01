package org.example.javaprojektsystemrezerwacjihotelowej.security;

import org.example.javaprojektsystemrezerwacjihotelowej.config.SecurityConfig;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.RoleName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for the security configuration.
 * This test verifies that the security components are properly configured in the application context.
 */
@SpringBootTest
public class SecurityFilterChainTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void securityComponents_ShouldBeConfiguredInApplicationContext() {
        // Verify that security components are available in the application context
        assertNotNull(applicationContext.getBean(SecurityConfig.class), 
                "SecurityConfig should be available in the application context");

        assertNotNull(applicationContext.getBean(UserDetailsService.class), 
                "UserDetailsService should be available in the application context");

        assertNotNull(applicationContext.getBean(AuthenticationManager.class), 
                "AuthenticationManager should be available in the application context");
    }
}
