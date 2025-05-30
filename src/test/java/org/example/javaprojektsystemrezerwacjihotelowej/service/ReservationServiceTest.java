package org.example.javaprojektsystemrezerwacjihotelowej.service;

import org.example.javaprojektsystemrezerwacjihotelowej.entity.Reservation;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.Room;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.User;
import org.example.javaprojektsystemrezerwacjihotelowej.repository.ReservationsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    @Mock
    private ReservationsRepository reservationsRepository;

    @Mock
    private RoomService roomService;

    @InjectMocks
    private ReservationService reservationService;

    private Reservation testReservation;
    private Room testRoom;
    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
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
        
        // Setup mocks
        when(roomService.getRoomById(1L)).thenReturn(testRoom);
        when(reservationsRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(reservationsRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(reservationsRepository.findAll()).thenReturn(Arrays.asList(testReservation));
    }

    @Test
    void getAllReservations_ShouldReturnAllReservations() {
        // Act
        List<Reservation> result = reservationService.getAllReservations();
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testReservation, result.get(0));
        
        // Verify repository was called
        verify(reservationsRepository, times(1)).findAll();
    }

    @Test
    void getReservationById_ShouldReturnReservationWhenExists() {
        // Act
        Reservation result = reservationService.getReservationById(1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(testReservation, result);
        
        // Verify repository was called
        verify(reservationsRepository, times(1)).findById(1L);
    }

    @Test
    void getReservationById_ShouldThrowExceptionWhenNotExists() {
        // Arrange
        when(reservationsRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> reservationService.getReservationById(999L)
        );
        
        assertEquals("Reservation not found with id 999", exception.getMessage());
        
        // Verify repository was called
        verify(reservationsRepository, times(1)).findById(999L);
    }

    @Test
    void createReservation_ShouldCalculatePriceAndSave() {
        // Arrange
        Reservation newReservation = new Reservation();
        newReservation.setUser(testUser);
        newReservation.setRoom(testRoom);
        newReservation.setCheckInDate(LocalDate.now());
        newReservation.setCheckOutDate(LocalDate.now().plusDays(3));
        
        // Act
        Reservation result = reservationService.createReservation(newReservation);
        
        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("300.0"), result.getTotalPrice());
        
        // Verify interactions
        verify(roomService, times(1)).getRoomById(1L);
        
        // Capture the reservation being saved to verify its properties
        ArgumentCaptor<Reservation> reservationCaptor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationsRepository).save(reservationCaptor.capture());
        Reservation savedReservation = reservationCaptor.getValue();
        
        assertEquals(testUser, savedReservation.getUser());
        assertEquals(testRoom, savedReservation.getRoom());
        assertEquals(LocalDate.now(), savedReservation.getCheckInDate());
        assertEquals(LocalDate.now().plusDays(3), savedReservation.getCheckOutDate());
        assertEquals(new BigDecimal("300.0"), savedReservation.getTotalPrice());
    }

    @Test
    void updateReservation_ShouldUpdateFieldsAndSave() {
        // Arrange
        Reservation updatedReservation = new Reservation();
        updatedReservation.setCheckInDate(LocalDate.now().plusDays(1));
        updatedReservation.setCheckOutDate(LocalDate.now().plusDays(5));
        updatedReservation.setStatus("CONFIRMED");
        updatedReservation.setSpecialRequests("Extra pillows");
        
        // Act
        Reservation result = reservationService.updateReservation(1L, updatedReservation);
        
        // Assert
        assertNotNull(result);
        assertEquals(LocalDate.now().plusDays(1), result.getCheckInDate());
        assertEquals(LocalDate.now().plusDays(5), result.getCheckOutDate());
        assertEquals("CONFIRMED", result.getStatus());
        assertEquals("Extra pillows", result.getSpecialRequests());
        
        // Verify interactions
        verify(reservationsRepository, times(1)).findById(1L);
        
        // Capture the reservation being saved to verify its properties
        ArgumentCaptor<Reservation> reservationCaptor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationsRepository).save(reservationCaptor.capture());
        Reservation savedReservation = reservationCaptor.getValue();
        
        assertEquals(LocalDate.now().plusDays(1), savedReservation.getCheckInDate());
        assertEquals(LocalDate.now().plusDays(5), savedReservation.getCheckOutDate());
        assertEquals("CONFIRMED", savedReservation.getStatus());
        assertEquals("Extra pillows", savedReservation.getSpecialRequests());
    }

    @Test
    void cancelReservation_ShouldSetStatusToCancelled() {
        // Act
        reservationService.cancelReservation(1L);
        
        // Assert
        // Capture the reservation being saved to verify its properties
        ArgumentCaptor<Reservation> reservationCaptor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationsRepository).save(reservationCaptor.capture());
        Reservation savedReservation = reservationCaptor.getValue();
        
        assertEquals("CANCELLED", savedReservation.getStatus());
        
        // Verify interactions
        verify(reservationsRepository, times(1)).findById(1L);
    }
}