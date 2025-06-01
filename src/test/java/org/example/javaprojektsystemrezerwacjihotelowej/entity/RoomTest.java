package org.example.javaprojektsystemrezerwacjihotelowej.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RoomTest {

    private Room room;
    private User user;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        // Create test user
        user = new User();
        user.setUser_id(1L);
        user.setUsername("testuser");
        user.setUsersurname("testsurname");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setPhone(1234567890L);

        // Create test room
        room = new Room();
        room.setRoomId(1L);
        room.setRoomNumber("101");
        room.setFloor("1");
        room.setNumberOfBeds(2);
        room.setPrice(100.0);

        // Create test reservation
        reservation = new Reservation();
        reservation.setReservationId(1L);
        reservation.setUser(user);
        reservation.setRoom(room);

        // Add reservation to room
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(reservation);
        room.setReservations(reservations);

        // Add user to room
        Set<User> users = new HashSet<>();
        users.add(user);
        room.setUsers(users);
    }

    @Test
    void testRoomCreation() {
        // Assert all fields are set correctly
        assertEquals(1L, room.getRoomId());
        assertEquals("101", room.getRoomNumber());
        assertEquals("1", room.getFloor());
        assertEquals(2, room.getNumberOfBeds());
        assertEquals(100.0, room.getPrice());

        // Assert collections are set correctly
        assertEquals(1, room.getUsers().size());
        assertTrue(room.getUsers().contains(user));

        assertEquals(1, room.getReservations().size());
        assertTrue(room.getReservations().contains(reservation));
    }

    @Test
    void testSetAndGetRoomNumber() {
        // Test setting and getting roomNumber
        room.setRoomNumber("102");
        assertEquals("102", room.getRoomNumber());
    }

    @Test
    void testSetAndGetFloor() {
        // Test setting and getting floor
        room.setFloor("2");
        assertEquals("2", room.getFloor());
    }

    @Test
    void testSetAndGetNumberOfBeds() {
        // Test setting and getting numberOfBeds
        room.setNumberOfBeds(3);
        assertEquals(3, room.getNumberOfBeds());
    }

    @Test
    void testSetAndGetPrice() {
        // Test setting and getting price
        room.setPrice(150.0);
        assertEquals(150.0, room.getPrice());
    }

    @Test
    void testAddAndRemoveUser() {
        // Create a new user
        User newUser = new User();
        newUser.setUser_id(2L);
        newUser.setUsername("newuser");
        newUser.setEmail("newuser@example.com");

        // Add the user to room
        Set<User> users = room.getUsers();
        users.add(newUser);
        room.setUsers(users);

        // Assert user was added
        assertEquals(2, room.getUsers().size());
        assertTrue(room.getUsers().contains(newUser));

        // Remove the user
        users.remove(newUser);
        room.setUsers(users);

        // Assert user was removed
        assertEquals(1, room.getUsers().size());
        assertFalse(room.getUsers().contains(newUser));
    }

    @Test
    void testAddAndRemoveReservation() {
        // Create a new reservation
        Reservation newReservation = new Reservation();
        newReservation.setReservationId(2L);
        newReservation.setRoom(room);

        // Add the reservation to room
        List<Reservation> reservations = room.getReservations();
        reservations.add(newReservation);
        room.setReservations(reservations);

        // Assert reservation was added
        assertEquals(2, room.getReservations().size());
        assertTrue(room.getReservations().contains(newReservation));

        // Remove the reservation
        reservations.remove(newReservation);
        room.setReservations(reservations);

        // Assert reservation was removed
        assertEquals(1, room.getReservations().size());
        assertFalse(room.getReservations().contains(newReservation));
    }
}