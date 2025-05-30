package org.example.javaprojektsystemrezerwacjihotelowej.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;
    private Role role;
    private Room room;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        // Create test role
        role = new Role();
        role.setId(1);
        role.setName(RoleName.USER);

        // Create test room
        room = new Room();
        room.setRoomId(1L);
        room.setRoomNumber("101");
        room.setPrice(100.0);

        // Create test user
        user = new User();
        user.setUser_id(1L);
        user.setUsername("testuser");
        user.setUsersurname("testsurname");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setPhone(1234567890L);

        // Add role to user
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        // Add room to user
        Set<Room> rooms = new HashSet<>();
        rooms.add(room);
        user.setRooms(rooms);

        // Create test reservation
        reservation = new Reservation();
        reservation.setReservationId(1L);
        reservation.setUser(user);
        reservation.setRoom(room);

        // Add reservation to user
        Set<Reservation> reservations = new HashSet<>();
        reservations.add(reservation);
        user.setReservations(reservations);
    }

    @Test
    void testUserCreation() {
        // Assert all fields are set correctly
        assertEquals(1L, user.getUser_id());
        assertEquals("testuser", user.getUsername());
        assertEquals("testsurname", user.getUsersurname());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals(1234567890L, user.getPhone());

        // Assert collections are set correctly
        assertEquals(1, user.getRoles().size());
        assertTrue(user.getRoles().contains(role));

        assertEquals(1, user.getRooms().size());
        assertTrue(user.getRooms().contains(room));

        assertEquals(1, user.getReservations().size());
        assertTrue(user.getReservations().contains(reservation));
    }

    @Test
    void testCreatedAtField() {
        // We can't directly call prePersist as it's private
        // Instead, we'll set createdAt manually for testing
        LocalDateTime testTime = LocalDateTime.now();
        java.lang.reflect.Field field;
        try {
            field = User.class.getDeclaredField("createdAt");
            field.setAccessible(true);
            field.set(user, testTime);
        } catch (Exception e) {
            fail("Failed to set createdAt field: " + e.getMessage());
        }

        // Assert createdAt is set
        assertEquals(testTime, user.getCreatedAt());
    }

    @Test
    void testSetAndGetUsername() {
        // Test setting and getting username
        user.setUsername("newusername");
        assertEquals("newusername", user.getUsername());
    }

    @Test
    void testSetAndGetUsersurname() {
        // Test setting and getting usersurname
        user.setUsersurname("newsurname");
        assertEquals("newsurname", user.getUsersurname());
    }

    @Test
    void testSetAndGetEmail() {
        // Test setting and getting email
        user.setEmail("newemail@example.com");
        assertEquals("newemail@example.com", user.getEmail());
    }

    @Test
    void testSetAndGetPassword() {
        // Test setting and getting password
        user.setPassword("newpassword123");
        assertEquals("newpassword123", user.getPassword());
    }

    @Test
    void testSetAndGetPhone() {
        // Test setting and getting phone
        user.setPhone(9876543210L);
        assertEquals(9876543210L, user.getPhone());
    }

    @Test
    void testAddAndRemoveRole() {
        // Create a new role
        Role adminRole = new Role();
        adminRole.setId(2);
        adminRole.setName(RoleName.ADMIN);

        // Add the role to user
        Set<Role> roles = user.getRoles();
        roles.add(adminRole);
        user.setRoles(roles);

        // Assert role was added
        assertEquals(2, user.getRoles().size());
        assertTrue(user.getRoles().contains(adminRole));

        // Remove the role
        roles.remove(adminRole);
        user.setRoles(roles);

        // Assert role was removed
        assertEquals(1, user.getRoles().size());
        assertFalse(user.getRoles().contains(adminRole));
    }

    @Test
    void testAddAndRemoveRoom() {
        // Create a new room
        Room newRoom = new Room();
        newRoom.setRoomId(2L);
        newRoom.setRoomNumber("102");

        // Add the room to user
        Set<Room> rooms = user.getRooms();
        rooms.add(newRoom);
        user.setRooms(rooms);

        // Assert room was added
        assertEquals(2, user.getRooms().size());
        assertTrue(user.getRooms().contains(newRoom));

        // Remove the room
        rooms.remove(newRoom);
        user.setRooms(rooms);

        // Assert room was removed
        assertEquals(1, user.getRooms().size());
        assertFalse(user.getRooms().contains(newRoom));
    }

    @Test
    void testAddAndRemoveReservation() {
        // Create a new reservation
        Reservation newReservation = new Reservation();
        newReservation.setReservationId(2L);
        newReservation.setUser(user);

        // Add the reservation to user
        Set<Reservation> reservations = user.getReservations();
        reservations.add(newReservation);
        user.setReservations(reservations);

        // Assert reservation was added
        assertEquals(2, user.getReservations().size());
        assertTrue(user.getReservations().contains(newReservation));

        // Remove the reservation
        reservations.remove(newReservation);
        user.setReservations(reservations);

        // Assert reservation was removed
        assertEquals(1, user.getReservations().size());
        assertFalse(user.getReservations().contains(newReservation));
    }
}
