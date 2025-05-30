package org.example.javaprojektsystemrezerwacjihotelowej.service.pricing;

import org.example.javaprojektsystemrezerwacjihotelowej.entity.Reservation;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.Room;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Strategy interface for calculating reservation prices.
 * This interface defines the contract for different pricing strategies.
 */
public interface PricingStrategy {
    
    /**
     * Calculate the total price for a reservation.
     *
     * @param room The room being reserved
     * @param checkInDate The check-in date
     * @param checkOutDate The check-out date
     * @return The calculated total price
     */
    BigDecimal calculatePrice(Room room, LocalDate checkInDate, LocalDate checkOutDate);
    
    /**
     * Get the name of the pricing strategy.
     *
     * @return The name of the strategy
     */
    String getStrategyName();
}