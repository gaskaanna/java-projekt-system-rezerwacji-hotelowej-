package org.example.javaprojektsystemrezerwacjihotelowej.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.Reservation;
import org.example.javaprojektsystemrezerwacjihotelowej.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Reservation Management", description = "Operations related to reservation management")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @Operation(summary = "Get all reservations", description = "Retrieve a list of all reservations.")
    @GetMapping
    public List<Reservation> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @Operation(summary = "Create a new reservation", description = "Add a new reservation to the hotel system.")
    @PostMapping
    public ResponseEntity<Reservation> createReservation (
            @RequestBody Reservation reservation
    ) {
        return ResponseEntity.ok(reservationService.createReservation(reservation));
    }

    @Operation(summary = "Update an existing reservation", description = "Update the details of an existing reservation.")
    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(
            @PathVariable Long id,
            @RequestBody Reservation reservation
    ) {
        return ResponseEntity.ok(reservationService.updateReservation(id, reservation));
    }

    @Operation(summary = "Cancel a reservation", description = "Cancel an existing reservation.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }
}