package org.example.javaprojektsystemrezerwacjihotelowej.service.pricing;

import org.example.javaprojektsystemrezerwacjihotelowej.entity.Room;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Discount pricing strategy that applies a discount for longer stays.
 * The discount percentage increases with the length of stay.
 */
@Component
public class DiscountPricingStrategy implements PricingStrategy {

    // Default discount percentages if not specified in application properties
    @Value("${pricing.discount.threshold.days:3}")
    private int discountThresholdDays;

    @Value("${pricing.discount.percentage:10}")
    private int discountPercentage;

    @Value("${pricing.discount.additional.percentage:5}")
    private int additionalDiscountPercentage;

    @Value("${pricing.discount.max.percentage:25}")
    private int maxDiscountPercentage;

    @Override
    public BigDecimal calculatePrice(Room room, LocalDate checkInDate, LocalDate checkOutDate) {
        // Calculate the number of days
        long days = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        
        // If days is 0 (same day check-in and check-out), charge for 1 day
        if (days <= 0) {
            days = 1;
        }
        
        // Calculate the base price: days * room price
        BigDecimal basePrice = BigDecimal.valueOf(days).multiply(BigDecimal.valueOf(room.getPrice()));
        
        // Apply discount based on length of stay
        if (days >= discountThresholdDays) {
            // Calculate discount percentage (increases with length of stay)
            int actualDiscount = Math.min(
                discountPercentage + ((int)(days - discountThresholdDays) * additionalDiscountPercentage),
                maxDiscountPercentage
            );
            
            // Apply the discount
            BigDecimal discountAmount = basePrice.multiply(BigDecimal.valueOf(actualDiscount))
                                               .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            return basePrice.subtract(discountAmount);
        }
        
        // No discount for short stays
        return basePrice;
    }

    @Override
    public String getStrategyName() {
        return "Discount";
    }
}