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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ReservationControllerTest {

    @Mock
    private ReservationService reservationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoomService roomService;

    @InjectMocks
    private ReservationController reservationController;

    private Reservation testReservation;
    private List<Reservation> testReservations;
    private Room testRoom;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setUser_id(1L);
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
        testReservation.setStatus("PENDING");
        testReservation.setTotalPrice(new BigDecimal("300.00"));

        // Create second test reservation
        Reservation reservation2 = new Reservation();
        reservation2.setReservationId(2L);
        reservation2.setUser(testUser);
        reservation2.setRoom(testRoom);
        reservation2.setCheckInDate(LocalDate.now().plusDays(10));
        reservation2.setCheckOutDate(LocalDate.now().plusDays(15));
        reservation2.setStatus("CONFIRMED");
        reservation2.setTotalPrice(new BigDecimal("500.00"));

        testReservations = Arrays.asList(testReservation, reservation2);

        // Setup mocks
        when(reservationService.getAllReservations()).thenReturn(testReservations);
        when(reservationService.getReservationById(1L)).thenReturn(testReservation);
        when(reservationService.createReservation(any(Reservation.class))).thenReturn(testReservation);
        when(reservationService.updateReservation(eq(1L), any(Reservation.class))).thenReturn(testReservation);
        doNothing().when(reservationService).cancelReservation(1L);

        // Setup UserRepository mock
        when(userRepository.findById(testUser.getUser_id())).thenReturn(java.util.Optional.of(testUser));

        // Setup RoomService mock
        when(roomService.getRoomById(testRoom.getRoomId())).thenReturn(testRoom);
    }

    @Test
    void getAllReservations_ShouldReturnAllReservations() {
        // Act
        List<Reservation> result = reservationController.getAllReservations();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testReservation, result.get(0));
        assertEquals(testReservations.get(1), result.get(1));

        // Verify service was called
        verify(reservationService, times(1)).getAllReservations();
    }

    @Test
    void createReservation_ShouldCreateAndReturnReservation() {
        // Arrange
        ReservationDTO reservationDTO = new ReservationDTO(
            testReservation.getCheckInDate(),
            testReservation.getCheckOutDate(),
            testReservation.getSpecialRequests(),
            testUser.getUser_id(),
            testRoom.getRoomId()
        );

        // Act
        ResponseEntity<Reservation> response = reservationController.createReservation(reservationDTO, null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testReservation, response.getBody());

        // Verify service was called
        verify(reservationService, times(1)).createReservation(any(Reservation.class));
    }

    @Test
    void updateReservation_ShouldUpdateAndReturnReservation() {
        // Arrange
        Reservation updatedReservation = new Reservation();
        updatedReservation.setCheckInDate(LocalDate.now().plusDays(1));
        updatedReservation.setCheckOutDate(LocalDate.now().plusDays(5));
        updatedReservation.setStatus("CONFIRMED");
        updatedReservation.setSpecialRequests("Extra pillows");

        // Act
        ResponseEntity<Reservation> response = reservationController.updateReservation(1L, updatedReservation, null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testReservation, response.getBody());

        // Verify service was called
        verify(reservationService, times(1)).updateReservation(1L, updatedReservation);
    }

    @Test
    void cancelReservation_ShouldReturnNoContent() {
        // Act
        ResponseEntity<Void> response = reservationController.cancelReservation(1L, null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        // Verify service was called
        verify(reservationService, times(1)).cancelReservation(1L);
    }
}
