package org.example.javaprojektsystemrezerwacjihotelowej.controller;

import org.example.javaprojektsystemrezerwacjihotelowej.dto.ReservationDTO;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.Reservation;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.Room;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.User;
import org.example.javaprojektsystemrezerwacjihotelowej.repository.UserRepository;
import org.example.javaprojektsystemrezerwacjihotelowej.service.ReservationService;
import org.example.javaprojektsystemrezerwacjihotelowej.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ReservationControllerAuthTest {

    @Mock
    private ReservationService reservationService;

    @Mock
    private RoomService roomService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReservationController reservationController;

    private User testUser;
    private User otherUser;
    private Room testRoom;
    private Reservation testReservation;
    private ReservationDTO testReservationDTO;
    private UserDetails adminUserDetails;
    private UserDetails regularUserDetails;
    private UserDetails otherUserDetails;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setUser_id(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        // Create other user
        otherUser = new User();
        otherUser.setUser_id(2L);
        otherUser.setUsername("otheruser");
        otherUser.setEmail("other@example.com");

        // Create test room
        testRoom = new Room();
        testRoom.setRoomId(1L);
        testRoom.setRoomNumber("101");
        testRoom.setPrice(100.0);

        // Create test reservation
        testReservation = new Reservation();
        testReservation.setReservationId(1L);
        testReservation.setUser(testUser);
        testReservation.setRoom(testRoom);
        testReservation.setCheckInDate(LocalDate.now());
        testReservation.setCheckOutDate(LocalDate.now().plusDays(3));
        testReservation.setSpecialRequests("No smoking room");

        // Create test reservation DTO
        testReservationDTO = new ReservationDTO(
                LocalDate.now(),
                LocalDate.now().plusDays(3),
                "No smoking room",
                1L,
                1L
        );

        // Create mock user details
        adminUserDetails = new org.springframework.security.core.userdetails.User(
                "admin@example.com",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        regularUserDetails = new org.springframework.security.core.userdetails.User(
                "test@example.com",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        otherUserDetails = new org.springframework.security.core.userdetails.User(
                "other@example.com",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        // Setup mock responses
        when(reservationService.getReservationById(1L)).thenReturn(testReservation);
        when(reservationService.createReservation(any(Reservation.class))).thenReturn(testReservation);
        when(reservationService.updateReservation(eq(1L), any(Reservation.class))).thenReturn(testReservation);
        doNothing().when(reservationService).cancelReservation(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(otherUser));
        when(roomService.getRoomById(1L)).thenReturn(testRoom);
    }

    @Test
    void createReservation_AsAdmin_ForAnyUser_ShouldSucceed() {
        // Create DTO for other user
        ReservationDTO otherUserDTO = new ReservationDTO(
                LocalDate.now(),
                LocalDate.now().plusDays(3),
                "No smoking room",
                2L,
                1L
        );

        // Act
        ResponseEntity<Reservation> response = reservationController.createReservation(otherUserDTO, adminUserDetails);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testReservation, response.getBody());
    }

    @Test
    void createReservation_AsUser_ForOtherUser_ShouldFail() {
        // Create DTO for other user
        ReservationDTO otherUserDTO = new ReservationDTO(
                LocalDate.now(),
                LocalDate.now().plusDays(3),
                "No smoking room",
                2L,
                1L
        );

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            reservationController.createReservation(otherUserDTO, regularUserDetails);
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertTrue(exception.getReason().contains("You can only create reservations for your own account"));
    }

    @Test
    void updateReservation_AsAdmin_ForAnyUser_ShouldSucceed() {
        // Act
        ResponseEntity<Reservation> response = reservationController.updateReservation(1L, testReservation, adminUserDetails);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testReservation, response.getBody());
    }

    @Test
    void updateReservation_AsUser_ForOwnReservation_ShouldSucceed() {
        // Act
        ResponseEntity<Reservation> response = reservationController.updateReservation(1L, testReservation, regularUserDetails);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testReservation, response.getBody());
    }

    @Test
    void updateReservation_AsUser_ForOtherUserReservation_ShouldFail() {
        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            reservationController.updateReservation(1L, testReservation, otherUserDetails);
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertTrue(exception.getReason().contains("You can only update your own reservations"));
    }

    @Test
    void cancelReservation_AsAdmin_ForAnyUser_ShouldSucceed() {
        // Act
        ResponseEntity<Void> response = reservationController.cancelReservation(1L, adminUserDetails);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(reservationService, times(1)).cancelReservation(1L);
    }

    @Test
    void cancelReservation_AsUser_ForOwnReservation_ShouldSucceed() {
        // Act
        ResponseEntity<Void> response = reservationController.cancelReservation(1L, regularUserDetails);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(reservationService, times(1)).cancelReservation(1L);
    }

    @Test
    void cancelReservation_AsUser_ForOtherUserReservation_ShouldFail() {
        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            reservationController.cancelReservation(1L, otherUserDetails);
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertTrue(exception.getReason().contains("You can only cancel your own reservations"));
    }
}
