package org.example.javaprojektsystemrezerwacjihotelowej.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    private Role role;

    @BeforeEach
    void setUp() {
        // Create test role
        role = new Role();
        role.setId(1);
        role.setName(RoleName.USER);
    }

    @Test
    void testRoleCreation() {
        // Assert all fields are set correctly
        assertEquals(1, role.getId());
        assertEquals(RoleName.USER, role.getName());
    }

    @Test
    void testSetAndGetId() {
        // Test setting and getting id
        role.setId(2);
        assertEquals(2, role.getId());
    }

    @Test
    void testSetAndGetName() {
        // Test setting and getting name
        role.setName(RoleName.ADMIN);
        assertEquals(RoleName.ADMIN, role.getName());
    }

    @Test
    void testAllRoleNames() {
        // Test all possible role names
        role.setName(RoleName.USER);
        assertEquals(RoleName.USER, role.getName());

        role.setName(RoleName.ADMIN);
        assertEquals(RoleName.ADMIN, role.getName());

        role.setName(RoleName.MENAGER);
        assertEquals(RoleName.MENAGER, role.getName());
    }

    @Test
    void testBuilderPattern() {
        // Test the builder pattern
        Role builtRole = Role.builder()
                .id(3)
                .name(RoleName.MENAGER)
                .build();

        assertEquals(3, builtRole.getId());
        assertEquals(RoleName.MENAGER, builtRole.getName());
    }

    @Test
    void testAllArgsConstructor() {
        // Test the all args constructor
        Role constructedRole = new Role(4, RoleName.ADMIN);

        assertEquals(4, constructedRole.getId());
        assertEquals(RoleName.ADMIN, constructedRole.getName());
    }
}
