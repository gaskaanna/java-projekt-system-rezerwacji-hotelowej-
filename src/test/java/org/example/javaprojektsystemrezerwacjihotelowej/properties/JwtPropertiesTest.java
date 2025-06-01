package org.example.javaprojektsystemrezerwacjihotelowej.properties;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the JwtProperties class.
 * These tests verify that the JWT properties are correctly loaded from application.properties.
 */
@SpringBootTest
public class JwtPropertiesTest {

    @Autowired
    private JwtProperties jwtProperties;

    @Test
    public void jwtProperties_ShouldLoadCorrectValues() {
        // Assert that the properties are not null
        assertNotNull(jwtProperties, "JwtProperties should not be null");
        assertNotNull(jwtProperties.getSecret(), "JWT secret should not be null");
        
        // Assert that the properties have the expected values from application.properties
        assertEquals("YTJjZDhkNTIzZTA1M2E1NTY0NGU0MDA0YzY1NjJkNjk=", jwtProperties.getSecret(), 
                "JWT secret should match the value in application.properties");
        assertEquals(3600000L, jwtProperties.getExpirationMs(), 
                "JWT expiration time should match the value in application.properties");
        assertEquals(604800000L, jwtProperties.getRefreshExpMs(), 
                "JWT refresh expiration time should match the value in application.properties");
    }

    @Test
    public void jwtProperties_ShouldHaveReasonableValues() {
        // Assert that the expiration times are reasonable
        assertTrue(jwtProperties.getExpirationMs() > 0, 
                "JWT expiration time should be positive");
        assertTrue(jwtProperties.getRefreshExpMs() > 0, 
                "JWT refresh expiration time should be positive");
        
        // Refresh token should have a longer expiration time than the access token
        assertTrue(jwtProperties.getRefreshExpMs() > jwtProperties.getExpirationMs(), 
                "Refresh token expiration time should be longer than access token expiration time");
    }
}