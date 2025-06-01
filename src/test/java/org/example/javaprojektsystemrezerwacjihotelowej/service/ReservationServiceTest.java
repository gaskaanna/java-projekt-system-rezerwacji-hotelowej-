package org.example.javaprojektsystemrezerwacjihotelowej.service;

import org.example.javaprojektsystemrezerwacjihotelowej.entity.Reservation;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.Room;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.User;
import org.example.javaprojektsystemrezerwacjihotelowej.repository.ReservationsRepository;
import org.example.javaprojektsystemrezerwacjihotelowej.service.pricing.PricingStrategy;
import org.example.javaprojektsystemrezerwacjihotelowej.service.pricing.PricingStrategyFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ReservationServiceTest {

    @Mock
    private ReservationsRepository reservationsRepository;

    @Mock
    private RoomService roomService;

    @Mock
    private PricingStrategyFactory pricingStrategyFactory;

    @Mock
    private PricingStrategy pricingStrategy;

    @InjectMocks
    private ReservationService reservationService;

    private Reservation reservation;
    private Room room;
    private User user;
    private List<Reservation> reservationList;

    @BeforeEach
    void setUp() {
        // Create test room
        room = new Room();
        room.setRoomId(1L);
        room.setRoomNumber("101");
        room.setNumberOfBeds(2);
        room.setPrice(100.0);

        // Create test user
        user = new User();
        user.setUser_id(1L);
        user.setEmail("test@example.com");
        user.setUsername("testuser");

        // Create test reservation
        reservation = new Reservation();
        reservation.setReservationId(1L);
        reservation.setRoom(room);
        reservation.setUser(user);
        reservation.setCheckInDate(LocalDate.now().plusDays(1));
        reservation.setCheckOutDate(LocalDate.now().plusDays(3));
        reservation.setStatus("PENDING");
        reservation.setTotalPrice(BigDecimal.valueOf(200.0));
        reservation.setCreatedAt(LocalDateTime.now());

        // Create test reservation list
        reservationList = Arrays.asList(reservation);

        // Configure mocks
        when(reservationsRepository.findAll()).thenReturn(reservationList);
        when(reservationsRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationsRepository.save(any(Reservation.class))).thenReturn(reservation);
        when(roomService.getRoomById(1L)).thenReturn(room);
        when(pricingStrategyFactory.getStrategy(anyString())).thenReturn(pricingStrategy);
        when(pricingStrategyFactory.getStrategy(any(LocalDate.class), any(LocalDate.class))).thenReturn(pricingStrategy);
        when(pricingStrategy.calculatePrice(any(Room.class), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(BigDecimal.valueOf(200.0));
    }

    @Test
    void getAllReservations_ShouldReturnAllReservations() {
        // Act
        List<Reservation> result = reservationService.getAllReservations();

        // Assert
        assertEquals(1, result.size());
        assertEquals(reservation, result.get(0));
        verify(reservationsRepository).findAll();
    }

    @Test
    void getReservationById_WithValidId_ShouldReturnReservation() {
        // Act
        Reservation result = reservationService.getReservationById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getReservationId());
        verify(reservationsRepository).findById(1L);
    }

    @Test
    void getReservationById_WithInvalidId_ShouldThrowException() {
        // Arrange
        when(reservationsRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reservationService.getReservationById(999L);
        });
        assertEquals("Reservation not found with id 999", exception.getMessage());
        verify(reservationsRepository).findById(999L);
    }

    @Test
    void createReservation_WithoutStrategy_ShouldUseAutoSelectedStrategy() {
        // Act
        Reservation result = reservationService.createReservation(reservation);

        // Assert
        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(200.0), result.getTotalPrice());
        verify(pricingStrategyFactory).getStrategy(any(LocalDate.class), any(LocalDate.class));
        verify(pricingStrategy).calculatePrice(any(Room.class), any(LocalDate.class), any(LocalDate.class));
        verify(reservationsRepository).save(reservation);
    }

    @Test
    void createReservation_WithStrategy_ShouldUseSpecifiedStrategy() {
        // Act
        Reservation result = reservationService.createReservation(reservation, "DISCOUNT");

        // Assert
        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(200.0), result.getTotalPrice());
        verify(pricingStrategyFactory).getStrategy("DISCOUNT");
        verify(pricingStrategy).calculatePrice(any(Room.class), any(LocalDate.class), any(LocalDate.class));
        verify(reservationsRepository).save(reservation);
    }

    @Test
    void updateReservation_ShouldUpdateAndReturnReservation() {
        // Arrange
        Reservation updatedReservation = new Reservation();
        updatedReservation.setCheckInDate(LocalDate.now().plusDays(5));
        updatedReservation.setCheckOutDate(LocalDate.now().plusDays(7));
        updatedReservation.setStatus("CONFIRMED");
        updatedReservation.setSpecialRequests("Late check-in");

        // Act
        Reservation result = reservationService.updateReservation(1L, updatedReservation);

        // Assert
        assertNotNull(result);
        assertEquals(updatedReservation.getCheckInDate(), result.getCheckInDate());
        assertEquals(updatedReservation.getCheckOutDate(), result.getCheckOutDate());
        assertEquals(updatedReservation.getStatus(), result.getStatus());
        assertEquals(updatedReservation.getSpecialRequests(), result.getSpecialRequests());
        verify(reservationsRepository).findById(1L);
        verify(reservationsRepository).save(any(Reservation.class));
    }

    @Test
    void cancelReservation_ShouldSetStatusToCancelled() {
        // Act
        reservationService.cancelReservation(1L);

        // Assert
        assertEquals("CANCELLED", reservation.getStatus());
        verify(reservationsRepository).findById(1L);
        verify(reservationsRepository).save(reservation);
    }
}
