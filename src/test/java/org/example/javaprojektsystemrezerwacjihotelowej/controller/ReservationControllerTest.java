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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ReservationControllerTest {

    @Mock
    private ReservationService reservationService;

    @Mock
    private RoomService roomService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReservationController reservationController;

    private User testUser;
    private Room testRoom;
    private Reservation testReservation;
    private ReservationDTO testReservationDTO;
    private List<Reservation> testReservations;
    private UserDetails adminUserDetails;
    private UserDetails regularUserDetails;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setUser_id(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

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

        // Create test reservations list
        testReservations = Arrays.asList(testReservation);

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

        // Setup mock responses
        when(reservationService.getAllReservations()).thenReturn(testReservations);
        when(reservationService.getReservationById(1L)).thenReturn(testReservation);
        when(reservationService.createReservation(any(Reservation.class))).thenReturn(testReservation);
        when(reservationService.updateReservation(eq(1L), any(Reservation.class))).thenReturn(testReservation);
        doNothing().when(reservationService).cancelReservation(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(roomService.getRoomById(1L)).thenReturn(testRoom);
    }

    @Test
    void getAllReservations_ShouldReturnAllReservations() {
        // Act
        List<Reservation> result = reservationController.getAllReservations();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testReservation, result.get(0));

        // Verify service was called
        verify(reservationService, times(1)).getAllReservations();
    }

    @Test
    void createReservation_WithValidData_ShouldCreateAndReturnReservation() {
        // Act
        ResponseEntity<Reservation> response = reservationController.createReservation(testReservationDTO, regularUserDetails);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testReservation, response.getBody());

        // Verify service was called
        verify(reservationService, times(1)).createReservation(any(Reservation.class));
    }

    @Test
    void updateReservation_WithValidData_ShouldUpdateAndReturnReservation() {
        // Act
        ResponseEntity<Reservation> response = reservationController.updateReservation(1L, testReservation, regularUserDetails);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testReservation, response.getBody());

        // Verify service was called
        verify(reservationService, times(1)).updateReservation(eq(1L), any(Reservation.class));
    }

    @Test
    void cancelReservation_WithValidId_ShouldReturnNoContent() {
        // Act
        ResponseEntity<Void> response = reservationController.cancelReservation(1L, regularUserDetails);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        // Verify service was called
        verify(reservationService, times(1)).cancelReservation(1L);
    }
}
