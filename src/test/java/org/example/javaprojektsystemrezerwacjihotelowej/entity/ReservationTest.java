package org.example.javaprojektsystemrezerwacjihotelowej.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReservationTest {

    private Reservation reservation;
    private User user;
    private Room room;

    @BeforeEach
    void setUp() {
        // Create test user
        user = new User();
        user.setUser_id(1L);
        user.setEmail("test@example.com");
        user.setUsername("testuser");

        // Create test room
        room = new Room();
        room.setRoomId(1L);
        room.setRoomNumber("101");
        room.setPrice(100.0);

        // Create test reservation
        reservation = new Reservation();
        reservation.setReservationId(1L);
        reservation.setCheckInDate(LocalDate.now());
        reservation.setCheckOutDate(LocalDate.now().plusDays(3));
        reservation.setTotalPrice(new BigDecimal("300.00"));
        reservation.setUser(user);
        reservation.setRoom(room);
        reservation.setSpecialRequests("Extra pillows");
    }

    @Test
    void testReservationCreation() {
        // Assert all fields are set correctly
        assertEquals(1L, reservation.getReservationId());
        assertEquals(LocalDate.now(), reservation.getCheckInDate());
        assertEquals(LocalDate.now().plusDays(3), reservation.getCheckOutDate());
        assertEquals(new BigDecimal("300.00"), reservation.getTotalPrice());
        assertEquals("Extra pillows", reservation.getSpecialRequests());
        assertEquals(user, reservation.getUser());
        assertEquals(room, reservation.getRoom());
    }

    @Test
    void testOnCreate() {
        // Manually trigger the onCreate method
        reservation.onCreate();

        // Assert createdAt is set
        assertNotNull(reservation.getCreatedAt());

        // Test default status is set to PENDING
        assertEquals("PENDING", reservation.getStatus());

        // Test calling onCreate again doesn't change status
        reservation.onCreate();
        assertEquals("PENDING", reservation.getStatus());

        // Test status is not changed if already set
        reservation.setStatus("CONFIRMED");
        reservation.onCreate();
        assertEquals("CONFIRMED", reservation.getStatus());
    }

    @Test
    void testOnUpdate() {
        // Manually trigger the onUpdate method
        reservation.onUpdate();

        // Assert updatedAt is set
        assertNotNull(reservation.getUpdatedAt());

        // Verify updatedAt is close to now
        LocalDateTime now = LocalDateTime.now();
        assertTrue(reservation.getUpdatedAt().isAfter(now.minusSeconds(1)));
        assertTrue(reservation.getUpdatedAt().isBefore(now.plusSeconds(1)));
    }

    @Test
    void testSetAndGetStatus() {
        // Test setting and getting status
        reservation.setStatus("CONFIRMED");
        assertEquals("CONFIRMED", reservation.getStatus());

        reservation.setStatus("CANCELLED");
        assertEquals("CANCELLED", reservation.getStatus());
    }

    @Test
    void testSetAndGetSpecialRequests() {
        // Test setting and getting special requests
        reservation.setSpecialRequests("Late check-in");
        assertEquals("Late check-in", reservation.getSpecialRequests());

        reservation.setSpecialRequests(null);
        assertNull(reservation.getSpecialRequests());
    }

    @Test
    void testSetAndGetDates() {
        // Test setting and getting dates
        LocalDate newCheckIn = LocalDate.now().plusDays(1);
        LocalDate newCheckOut = LocalDate.now().plusDays(5);

        reservation.setCheckInDate(newCheckIn);
        reservation.setCheckOutDate(newCheckOut);

        assertEquals(newCheckIn, reservation.getCheckInDate());
        assertEquals(newCheckOut, reservation.getCheckOutDate());
    }

    @Test
    void testSetAndGetTotalPrice() {
        // Test setting and getting total price
        BigDecimal newPrice = new BigDecimal("450.00");
        reservation.setTotalPrice(newPrice);
        assertEquals(newPrice, reservation.getTotalPrice());
    }
}
