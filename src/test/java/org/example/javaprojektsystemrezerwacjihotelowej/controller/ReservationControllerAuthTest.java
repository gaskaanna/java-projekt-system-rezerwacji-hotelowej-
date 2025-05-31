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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ReservationControllerAuthTest {

    @Mock
    private ReservationService reservationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoomService roomService;

    @InjectMocks
    private ReservationController reservationController;

    private Reservation testReservation;
    private User testUser;
    private User otherUser;
    private UserDetails adminUserDetails;
    private UserDetails regularUserDetails;
    private UserDetails managerUserDetails;

    @BeforeEach
    void setUp() {
        // Create test users
        testUser = new User();
        testUser.setUser_id(1L);
        testUser.setEmail("test@example.com");

        otherUser = new User();
        otherUser.setUser_id(2L);
        otherUser.setEmail("other@example.com");

        // Create test room
        Room testRoom = new Room();
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

        // Setup mock user details
        adminUserDetails = createUserDetails("test@example.com", "ROLE_ADMIN");
        regularUserDetails = createUserDetails("test@example.com", "ROLE_USER");
        managerUserDetails = createUserDetails("test@example.com", "ROLE_MENAGER");

        // Setup mocks
        when(reservationService.getReservationById(1L)).thenReturn(testReservation);
        when(reservationService.createReservation(any(Reservation.class))).thenReturn(testReservation);
        when(reservationService.updateReservation(eq(1L), any(Reservation.class))).thenReturn(testReservation);
        doNothing().when(reservationService).cancelReservation(1L);

        // Setup UserRepository mock
        when(userRepository.findById(testUser.getUser_id())).thenReturn(java.util.Optional.of(testUser));
        when(userRepository.findById(otherUser.getUser_id())).thenReturn(java.util.Optional.of(otherUser));

        // Setup RoomService mock
        when(roomService.getRoomById(1L)).thenReturn(testReservation.getRoom());
    }

    private UserDetails createUserDetails(String username, String... roles) {
        return new UserDetails() {
            @Override
            public Collection<SimpleGrantedAuthority> getAuthorities() {
                return Arrays.stream(roles)
                        .map(SimpleGrantedAuthority::new)
                        .toList();
            }

            @Override
            public String getPassword() {
                return "password";
            }

            @Override
            public String getUsername() {
                return username;
            }

            @Override
            public boolean isAccountNonExpired() {
                return true;
            }

            @Override
            public boolean isAccountNonLocked() {
                return true;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return true;
            }

            @Override
            public boolean isEnabled() {
                return true;
            }
        };
    }

    @Test
    void createReservation_AsAdmin_ForOtherUser_ShouldSucceed() {
        // Arrange
        ReservationDTO newReservationDTO = new ReservationDTO(
            LocalDate.now(),
            LocalDate.now().plusDays(3),
            "Special requests",
            otherUser.getUser_id(),
            1L // Using a dummy room ID
        );

        // Mock the service to return testReservation when createReservation is called with any Reservation
        when(reservationService.createReservation(any(Reservation.class))).thenReturn(testReservation);

        // Act
        ResponseEntity<Reservation> response = reservationController.createReservation(newReservationDTO, adminUserDetails);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(reservationService).createReservation(any(Reservation.class));
    }

    @Test
    void createReservation_AsUser_ForSelf_ShouldSucceed() {
        // Arrange
        ReservationDTO newReservationDTO = new ReservationDTO(
            LocalDate.now(),
            LocalDate.now().plusDays(3),
            "Special requests",
            testUser.getUser_id(),
            1L // Using a dummy room ID
        );

        // Mock the service to return testReservation when createReservation is called with any Reservation
        when(reservationService.createReservation(any(Reservation.class))).thenReturn(testReservation);

        // Act
        ResponseEntity<Reservation> response = reservationController.createReservation(newReservationDTO, regularUserDetails);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(reservationService).createReservation(any(Reservation.class));
    }

    @Test
    void createReservation_AsUser_ForOtherUser_ShouldThrowForbidden() {
        // Arrange
        ReservationDTO newReservationDTO = new ReservationDTO(
            LocalDate.now(),
            LocalDate.now().plusDays(3),
            "Special requests",
            otherUser.getUser_id(),
            1L // Using a dummy room ID
        );

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            reservationController.createReservation(newReservationDTO, regularUserDetails);
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("You can only create reservations for your own account", exception.getReason());
        verify(reservationService, never()).createReservation(any());
    }

    @Test
    void updateReservation_AsAdmin_ForOtherUser_ShouldSucceed() {
        // Arrange
        Reservation updatedReservation = new Reservation();
        updatedReservation.setUser(otherUser);

        // Act
        ResponseEntity<Reservation> response = reservationController.updateReservation(1L, updatedReservation, adminUserDetails);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(reservationService).updateReservation(eq(1L), any(Reservation.class));
    }

    @Test
    void updateReservation_AsUser_ForOtherUser_ShouldThrowForbidden() {
        // Arrange
        Reservation updatedReservation = new Reservation();

        // Change the user email to make ownership check fail
        UserDetails otherUserDetails = createUserDetails("other@example.com", "ROLE_USER");

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            reservationController.updateReservation(1L, updatedReservation, otherUserDetails);
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("You can only update your own reservations", exception.getReason());
        verify(reservationService, never()).updateReservation(anyLong(), any());
    }

    @Test
    void cancelReservation_AsAdmin_ForOtherUser_ShouldSucceed() {
        // Act
        ResponseEntity<Void> response = reservationController.cancelReservation(1L, adminUserDetails);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(reservationService).cancelReservation(1L);
    }

    @Test
    void cancelReservation_AsManager_ForOtherUser_ShouldSucceed() {
        // Act
        ResponseEntity<Void> response = reservationController.cancelReservation(1L, managerUserDetails);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(reservationService).cancelReservation(1L);
    }

    @Test
    void cancelReservation_AsUser_ForOtherUser_ShouldThrowForbidden() {
        // Arrange
        UserDetails otherUserDetails = createUserDetails("other@example.com", "ROLE_USER");

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            reservationController.cancelReservation(1L, otherUserDetails);
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("You can only cancel your own reservations", exception.getReason());
        verify(reservationService, never()).cancelReservation(anyLong());
    }
}
