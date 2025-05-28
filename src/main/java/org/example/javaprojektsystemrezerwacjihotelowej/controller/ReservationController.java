package org.example.javaprojektsystemrezerwacjihotelowej.controller;

import org.example.javaprojektsystemrezerwacjihotelowej.entity.Reservation;
import org.example.javaprojektsystemrezerwacjihotelowej.repository.ReservationsRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Reservation Management", description = "Operations related to reservation management")
public class ReservationController {

    @Autowired
    private ReservationsRepository reservationsRepository;

    @Operation(summary = "Get all reservations", description = "Retrieve a list of all reservations.")
    @GetMapping
    public List<Reservation> getAllReservations() {
        return reservationsRepository.findAll();
    }

    @Operation(summary = "Create a new reservation", description = "Add a new reservation to the hotel system.")
    @PostMapping
    public ResponseEntity<Reservation> createReservation(
            @Parameter(description = "Reservation object to be created", required = true)
            @RequestBody Reservation reservation) {
        Reservation savedReservation = reservationsRepository.save(reservation);
        return ResponseEntity.ok(savedReservation);
    }
}