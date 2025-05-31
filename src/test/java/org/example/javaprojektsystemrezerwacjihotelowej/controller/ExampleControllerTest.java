package org.example.javaprojektsystemrezerwacjihotelowej.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ExampleControllerTest {

    @InjectMocks
    private ExampleController exampleController;

    @BeforeEach
    void setUp() {
        // No setup needed as the controller doesn't have dependencies to mock
    }

    @Test
    void adminOnlyEndpoint_ShouldReturnExpectedMessage() {
        // Act
        ResponseEntity<String> response = exampleController.adminOnlyEndpoint();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("This endpoint can only be accessed by ADMIN users", response.getBody());
    }

    @Test
    void adminOrManagerEndpoint_ShouldReturnExpectedMessage() {
        // Act
        ResponseEntity<String> response = exampleController.adminOrManagerEndpoint();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("This endpoint can be accessed by ADMIN or MANAGER users", response.getBody());
    }

    @Test
    void userResourceEndpoint_ShouldReturnExpectedMessage() {
        // Arrange
        Long resourceId = 123L;

        // Act
        ResponseEntity<String> response = exampleController.userResourceEndpoint(resourceId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("This endpoint can be accessed by the owner of resource 123 or ADMIN users", response.getBody());
    }

    @Test
    void managerConfirmEndpoint_ShouldReturnExpectedMessage() {
        // Arrange
        Long reservationId = 456L;

        // Act
        ResponseEntity<String> response = exampleController.managerConfirmEndpoint(reservationId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("This endpoint allows MANAGER users to confirm reservation 456", response.getBody());
    }

    @Test
    void cancelReservationEndpoint_ShouldReturnExpectedMessage() {
        // Arrange
        Long reservationId = 789L;

        // Act
        ResponseEntity<String> response = exampleController.cancelReservationEndpoint(reservationId, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("This endpoint allows users to cancel their own reservations, or ADMIN/MANAGER users to cancel any reservation", response.getBody());
    }
}