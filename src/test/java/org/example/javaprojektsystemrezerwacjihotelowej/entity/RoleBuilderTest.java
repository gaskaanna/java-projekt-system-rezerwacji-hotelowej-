package org.example.javaprojektsystemrezerwacjihotelowej.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Role.RoleBuilder class.
 * These tests verify that the builder correctly creates Role objects with all fields.
 */
class RoleBuilderTest {

    @Test
    void testRoleBuilder_WithAllFields() {
        // Arrange & Act
        Role role = Role.builder()
                .id(1)
                .name(RoleName.USER)
                .build();

        // Assert
        assertEquals(1, role.getId());
        assertEquals(RoleName.USER, role.getName());
    }

    @Test
    void testRoleBuilder_WithIdOnly() {
        // Arrange & Act
        Role role = Role.builder()
                .id(2)
                .build();

        // Assert
        assertEquals(2, role.getId());
        assertNull(role.getName());
    }

    @Test
    void testRoleBuilder_WithNameOnly() {
        // Arrange & Act
        Role role = Role.builder()
                .name(RoleName.ADMIN)
                .build();

        // Assert
        assertNull(role.getId());
        assertEquals(RoleName.ADMIN, role.getName());
    }

    @Test
    void testRoleBuilder_WithNoFields() {
        // Arrange & Act
        Role role = Role.builder().build();

        // Assert
        assertNull(role.getId());
        assertNull(role.getName());
    }

    @Test
    void testRoleBuilder_WithDifferentRoleNames() {
        // Test USER role
        Role userRole = Role.builder()
                .name(RoleName.USER)
                .build();
        assertEquals(RoleName.USER, userRole.getName());

        // Test ADMIN role
        Role adminRole = Role.builder()
                .name(RoleName.ADMIN)
                .build();
        assertEquals(RoleName.ADMIN, adminRole.getName());

        // Test MENAGER role
        Role managerRole = Role.builder()
                .name(RoleName.MENAGER)
                .build();
        assertEquals(RoleName.MENAGER, managerRole.getName());
    }
}