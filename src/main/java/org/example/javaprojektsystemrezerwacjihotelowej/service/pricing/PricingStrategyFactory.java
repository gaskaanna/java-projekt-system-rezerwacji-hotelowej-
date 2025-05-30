package org.example.javaprojektsystemrezerwacjihotelowej.service.pricing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Factory class for selecting the appropriate pricing strategy.
 * This class serves as the context in the Strategy pattern.
 */
@Component
public class PricingStrategyFactory {

    private final Map<String, PricingStrategy> strategies;
    
    @Autowired
    public PricingStrategyFactory(List<PricingStrategy> strategyList) {
        // Convert the list of strategies to a map with strategy name as key
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(PricingStrategy::getStrategyName, Function.identity()));
    }
    
    /**
     * Get the appropriate pricing strategy based on the length of stay.
     * For stays of 3 days or more, use the discount strategy.
     * Otherwise, use the standard strategy.
     *
     * @param checkInDate The check-in date
     * @param checkOutDate The check-out date
     * @return The selected pricing strategy
     */
    public PricingStrategy getStrategy(LocalDate checkInDate, LocalDate checkOutDate) {
        long days = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        
        // For longer stays, use the discount strategy
        if (days >= 3) {
            return strategies.getOrDefault("Discount", strategies.get("Standard"));
        }
        
        // For shorter stays, use the standard strategy
        return strategies.get("Standard");
    }
    
    /**
     * Get a specific pricing strategy by name.
     *
     * @param strategyName The name of the strategy to get
     * @return The requested pricing strategy, or the standard strategy if not found
     */
    public PricingStrategy getStrategy(String strategyName) {
        return strategies.getOrDefault(strategyName, strategies.get("Standard"));
    }
}