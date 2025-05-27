package org.example.javaprojektsystemrezerwacjihotelowej.controller;

import org.example.javaprojektsystemrezerwacjihotelowej.entity.Reservation;
import org.example.javaprojektsystemrezerwacjihotelowej.repository.ReservationsRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationsRepository reservationsRepository;

    @GetMapping
    public List<Reservation> getAllReservations() {
        return reservationsRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody Reservation reservation) {
        Reservation savedReservation = reservationsRepository.save(reservation);
        return ResponseEntity.ok(savedReservation);
    }
}