package org.example.javaprojektsystemrezerwacjihotelowej.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the User.UserBuilder class.
 * These tests verify that the builder correctly creates User objects with all fields.
 */
class UserBuilderTest {

    @Test
    void testUserBuilder_WithAllFields() {
        // Arrange
        Long userId = 1L;
        String username = "testuser";
        String usersurname = "testsurname";
        String email = "test@example.com";
        String password = "password123";
        Long phone = 1234567890L;
        Set<Role> roles = new HashSet<>();
        Set<Reservation> reservations = new HashSet<>();
        LocalDateTime createdAt = LocalDateTime.now();
        Set<Room> rooms = new HashSet<>();

        // Create a role
        Role role = new Role();
        role.setId(1);
        role.setName(RoleName.USER);
        roles.add(role);

        // Create a room
        Room room = new Room();
        room.setRoomId(1L);
        room.setRoomNumber("101");
        rooms.add(room);

        // Act
        User user = User.builder()
                .user_id(userId)
                .username(username)
                .usersurname(usersurname)
                .email(email)
                .password(password)
                .phone(phone)
                .roles(roles)
                .reservations(reservations)
                .createdAt(createdAt)
                .rooms(rooms)
                .build();

        // Assert
        assertEquals(userId, user.getUser_id());
        assertEquals(username, user.getUsername());
        assertEquals(usersurname, user.getUsersurname());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertEquals(phone, user.getPhone());
        assertEquals(roles, user.getRoles());
        assertEquals(reservations, user.getReservations());
        assertEquals(createdAt, user.getCreatedAt());
        assertEquals(rooms, user.getRooms());
    }

    @Test
    void testUserBuilder_WithRequiredFieldsOnly() {
        // Arrange
        String email = "test@example.com";
        String password = "password123";

        // Act
        User user = User.builder()
                .email(email)
                .password(password)
                .build();

        // Assert
        assertNull(user.getUser_id());
        assertNull(user.getUsername());
        assertNull(user.getUsersurname());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertNull(user.getPhone());
        assertNotNull(user.getRoles()); // Should be initialized with empty HashSet
        assertTrue(user.getRoles().isEmpty());
        assertNull(user.getReservations());
        assertNull(user.getCreatedAt());
        assertNull(user.getRooms());
    }

    @Test
    void testUserBuilder_WithDefaultRoles() {
        // Act
        User user = User.builder().build();

        // Assert
        assertNotNull(user.getRoles());
        assertTrue(user.getRoles().isEmpty());
    }

    @Test
    void testUserBuilder_WithCustomRoles() {
        // Arrange
        Set<Role> roles = new HashSet<>();
        Role role1 = new Role();
        role1.setId(1);
        role1.setName(RoleName.USER);
        Role role2 = new Role();
        role2.setId(2);
        role2.setName(RoleName.ADMIN);
        roles.add(role1);
        roles.add(role2);

        // Act
        User user = User.builder()
                .roles(roles)
                .build();

        // Assert
        assertEquals(roles, user.getRoles());
        assertEquals(2, user.getRoles().size());
        assertTrue(user.getRoles().contains(role1));
        assertTrue(user.getRoles().contains(role2));
    }

    @Test
    void testUserBuilder_WithNullRoles() {
        // Act
        User user = User.builder()
                .roles(null)
                .build();

        // Assert
        assertNull(user.getRoles());
    }

    @Test
    void testUserBuilder_WithReservations() {
        // Arrange
        Set<Reservation> reservations = new HashSet<>();
        Reservation reservation = new Reservation();
        reservation.setReservationId(1L);
        reservations.add(reservation);

        // Act
        User user = User.builder()
                .reservations(reservations)
                .build();

        // Assert
        assertEquals(reservations, user.getReservations());
        assertEquals(1, user.getReservations().size());
        assertTrue(user.getReservations().contains(reservation));
    }

    @Test
    void testUserBuilder_WithRooms() {
        // Arrange
        Set<Room> rooms = new HashSet<>();
        Room room = new Room();
        room.setRoomId(1L);
        room.setRoomNumber("101");
        rooms.add(room);

        // Act
        User user = User.builder()
                .rooms(rooms)
                .build();

        // Assert
        assertEquals(rooms, user.getRooms());
        assertEquals(1, user.getRooms().size());
        assertTrue(user.getRooms().contains(room));
    }
}