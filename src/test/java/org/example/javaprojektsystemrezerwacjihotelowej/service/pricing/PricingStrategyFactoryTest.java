package org.example.javaprojektsystemrezerwacjihotelowej.service.pricing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PricingStrategyFactoryTest {

    @Mock
    private StandardPricingStrategy standardStrategy;
    
    @Mock
    private DiscountPricingStrategy discountStrategy;
    
    private PricingStrategyFactory factory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Configure mocks
        when(standardStrategy.getStrategyName()).thenReturn("Standard");
        when(discountStrategy.getStrategyName()).thenReturn("Discount");
        
        // Create factory with mocked strategies
        factory = new PricingStrategyFactory(Arrays.asList(standardStrategy, discountStrategy));
    }

    @Test
    void getStrategy_WithShortStay_ShouldReturnStandardStrategy() {
        // Arrange
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = checkIn.plusDays(2); // 2 days, below threshold
        
        // Act
        PricingStrategy result = factory.getStrategy(checkIn, checkOut);
        
        // Assert
        assertSame(standardStrategy, result);
    }

    @Test
    void getStrategy_WithLongStay_ShouldReturnDiscountStrategy() {
        // Arrange
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = checkIn.plusDays(3); // 3 days, at threshold
        
        // Act
        PricingStrategy result = factory.getStrategy(checkIn, checkOut);
        
        // Assert
        assertSame(discountStrategy, result);
    }

    @Test
    void getStrategy_WithStandardName_ShouldReturnStandardStrategy() {
        // Act
        PricingStrategy result = factory.getStrategy("Standard");
        
        // Assert
        assertSame(standardStrategy, result);
    }

    @Test
    void getStrategy_WithDiscountName_ShouldReturnDiscountStrategy() {
        // Act
        PricingStrategy result = factory.getStrategy("Discount");
        
        // Assert
        assertSame(discountStrategy, result);
    }

    @Test
    void getStrategy_WithUnknownName_ShouldReturnStandardStrategy() {
        // Act
        PricingStrategy result = factory.getStrategy("Unknown");
        
        // Assert
        assertSame(standardStrategy, result);
    }
}