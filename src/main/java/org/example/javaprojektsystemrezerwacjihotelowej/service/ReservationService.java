// File: ReservationService.java
package org.example.javaprojektsystemrezerwacjihotelowej.service;

import lombok.RequiredArgsConstructor;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.Reservation;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.Room;
import org.example.javaprojektsystemrezerwacjihotelowej.repository.ReservationsRepository;
import org.example.javaprojektsystemrezerwacjihotelowej.service.pricing.PricingStrategy;
import org.example.javaprojektsystemrezerwacjihotelowej.service.pricing.PricingStrategyFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationsRepository reservationsRepository;
    private final RoomService roomService;
    private final PricingStrategyFactory pricingStrategyFactory;

    public List<Reservation> getAllReservations() {
        return reservationsRepository.findAll();
    }

    public Reservation getReservationById(Long id) {
        return reservationsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id " + id));
    }

    @Transactional
    public Reservation createReservation(Reservation reservation) {
        return createReservation(reservation, null);
    }

    /**
     * Create a reservation with a specific pricing strategy.
     * 
     * @param reservation The reservation to create
     * @param strategyName The name of the pricing strategy to use, or null to auto-select
     * @return The created reservation
     */
    @Transactional
    public Reservation createReservation(Reservation reservation, String strategyName) {
        // Pobierz pokój po ID
        Room room = roomService.getRoomById(reservation.getRoom().getRoomId());

        // Get the appropriate pricing strategy
        PricingStrategy pricingStrategy;
        if (strategyName != null) {
            // Use the specified strategy
            pricingStrategy = pricingStrategyFactory.getStrategy(strategyName);
        } else {
            // Auto-select strategy based on the reservation dates
            pricingStrategy = pricingStrategyFactory.getStrategy(
                    reservation.getCheckInDate(), 
                    reservation.getCheckOutDate()
            );
        }

        // Calculate the price using the selected strategy
        BigDecimal total = pricingStrategy.calculatePrice(
                room, 
                reservation.getCheckInDate(), 
                reservation.getCheckOutDate()
        );

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
