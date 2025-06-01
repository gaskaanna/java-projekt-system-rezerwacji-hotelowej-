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

    @Test
    public void jwtProperties_SettersAndGetters_ShouldWorkCorrectly() {
        // Create a new JwtProperties instance
        JwtProperties props = new JwtProperties();

        // Test setters
        props.setSecret("test-secret");
        props.setExpirationMs(1000L);
        props.setRefreshExpMs(2000L);

        // Test getters
        assertEquals("test-secret", props.getSecret(), "Secret getter should return the set value");
        assertEquals(1000L, props.getExpirationMs(), "ExpirationMs getter should return the set value");
        assertEquals(2000L, props.getRefreshExpMs(), "RefreshExpMs getter should return the set value");
    }

    @Test
    public void jwtProperties_EqualsAndHashCode_ShouldWorkCorrectly() {
        // Create two identical JwtProperties instances
        JwtProperties props1 = new JwtProperties();
        props1.setSecret("test-secret");
        props1.setExpirationMs(1000L);
        props1.setRefreshExpMs(2000L);

        JwtProperties props2 = new JwtProperties();
        props2.setSecret("test-secret");
        props2.setExpirationMs(1000L);
        props2.setRefreshExpMs(2000L);

        // Test equals
        assertEquals(props1, props2, "Equal JwtProperties instances should be equal");
        assertEquals(props1, props1, "JwtProperties instance should be equal to itself");
        assertNotEquals(props1, null, "JwtProperties instance should not be equal to null");
        assertNotEquals(props1, new Object(), "JwtProperties instance should not be equal to an object of a different class");

        // Test hashCode
        assertEquals(props1.hashCode(), props2.hashCode(), "Equal JwtProperties instances should have the same hash code");

        // Create a different JwtProperties instance
        JwtProperties props3 = new JwtProperties();
        props3.setSecret("different-secret");
        props3.setExpirationMs(1000L);
        props3.setRefreshExpMs(2000L);

        // Test not equals
        assertNotEquals(props1, props3, "Different JwtProperties instances should not be equal");

        // Test different hashCode
        assertNotEquals(props1.hashCode(), props3.hashCode(), "Different JwtProperties instances should have different hash codes");
    }

    @Test
    public void jwtProperties_ToString_ShouldContainAllFields() {
        // Create a JwtProperties instance
        JwtProperties props = new JwtProperties();
        props.setSecret("test-secret");
        props.setExpirationMs(1000L);
        props.setRefreshExpMs(2000L);

        // Test toString
        String toString = props.toString();
        assertTrue(toString.contains("secret=test-secret"), "toString should contain the secret field");
        assertTrue(toString.contains("expirationMs=1000"), "toString should contain the expirationMs field");
        assertTrue(toString.contains("refreshExpMs=2000"), "toString should contain the refreshExpMs field");
    }
}
