// File: ReservationService.java
package org.example.javaprojektsystemrezerwacjihotelowej.service;

import lombok.RequiredArgsConstructor;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.Reservation;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.Room;
import org.example.javaprojektsystemrezerwacjihotelowej.repository.ReservationsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationsRepository reservationsRepository;
    private final RoomService roomService;

    public List<Reservation> getAllReservations() {
        return reservationsRepository.findAll();
    }

    public Reservation getReservationById(Long id) {
        return reservationsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id " + id));
    }

    @Transactional
    public Reservation createReservation(Reservation reservation) {
        // Pobierz pokój po ID
        Room room = roomService.getRoomById(reservation.getRoom().getRoomId());
        // Oblicz liczbę dni
        long days = ChronoUnit.DAYS.between(
                reservation.getCheckInDate(),
                reservation.getCheckOutDate()
        );
        // Kalkulacja ceny
        BigDecimal total = BigDecimal.valueOf(days)
                .multiply(BigDecimal.valueOf(room.getPrice()));
        // Ustaw cenę (BigDecimal)
        reservation.setTotalPrice(total);
        return reservationsRepository.save(reservation);
    }

    @Transactional
    public Reservation updateReservation(Long id, Reservation updated) {
        Reservation existing = getReservationById(id);
        existing.setCheckInDate(updated.getCheckInDate());
        existing.setCheckOutDate(updated.getCheckOutDate());
        existing.setStatus(updated.getStatus());
        existing.setSpecialRequests(updated.getSpecialRequests());
        return reservationsRepository.save(existing);
    }

    @Transactional
    public void cancelReservation(Long id) {
        Reservation existing = getReservationById(id);
        existing.setStatus("CANCELLED");
        reservationsRepository.save(existing);
    }
}
