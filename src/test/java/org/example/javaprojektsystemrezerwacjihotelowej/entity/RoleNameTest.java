package org.example.javaprojektsystemrezerwacjihotelowej.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the RoleName enum.
 */
public class RoleNameTest {

    @Test
    public void testRoleNameValues() {
        // Test that the enum has the expected values
        assertEquals(3, RoleName.values().length, "RoleName should have 3 values");
        
        // Test that each value exists
        assertNotNull(RoleName.USER, "USER role should exist");
        assertNotNull(RoleName.ADMIN, "ADMIN role should exist");
        assertNotNull(RoleName.MENAGER, "MENAGER role should exist");
    }
    
    @Test
    public void testRoleNameOrdinals() {
        // Test the ordinal values
        assertEquals(0, RoleName.USER.ordinal(), "USER should have ordinal 0");
        assertEquals(1, RoleName.ADMIN.ordinal(), "ADMIN should have ordinal 1");
        assertEquals(2, RoleName.MENAGER.ordinal(), "MENAGER should have ordinal 2");
    }
    
    @Test
    public void testRoleNameToString() {
        // Test the string representation
        assertEquals("USER", RoleName.USER.toString(), "USER toString should be 'USER'");
        assertEquals("ADMIN", RoleName.ADMIN.toString(), "ADMIN toString should be 'ADMIN'");
        assertEquals("MENAGER", RoleName.MENAGER.toString(), "MENAGER toString should be 'MENAGER'");
    }
    
    @Test
    public void testRoleNameValueOf() {
        // Test valueOf method
        assertEquals(RoleName.USER, RoleName.valueOf("USER"), "valueOf('USER') should return USER");
        assertEquals(RoleName.ADMIN, RoleName.valueOf("ADMIN"), "valueOf('ADMIN') should return ADMIN");
        assertEquals(RoleName.MENAGER, RoleName.valueOf("MENAGER"), "valueOf('MENAGER') should return MENAGER");
    }
    
    @Test
    public void testRoleNameValueOfInvalid() {
        // Test valueOf with invalid value
        assertThrows(IllegalArgumentException.class, () -> RoleName.valueOf("INVALID_ROLE"),
                "valueOf with invalid role name should throw IllegalArgumentException");
    }
}