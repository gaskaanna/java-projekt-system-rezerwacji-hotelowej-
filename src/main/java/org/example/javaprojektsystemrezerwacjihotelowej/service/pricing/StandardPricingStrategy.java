package org.example.javaprojektsystemrezerwacjihotelowej.service.pricing;

import org.example.javaprojektsystemrezerwacjihotelowej.entity.Room;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Standard pricing strategy that calculates the price as (number of days * room price).
 * This is the default pricing strategy.
 */
@Component
public class StandardPricingStrategy implements PricingStrategy {

    @Override
    public BigDecimal calculatePrice(Room room, LocalDate checkInDate, LocalDate checkOutDate) {
        // Calculate the number of days
        long days = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        
        // If days is 0 (same day check-in and check-out), charge for 1 day
        if (days <= 0) {
            days = 1;
        }
        
        // Calculate the total price: days * room price
        return BigDecimal.valueOf(days).multiply(BigDecimal.valueOf(room.getPrice()));
    }

    @Override
    public String getStrategyName() {
        return "Standard";
    }
}