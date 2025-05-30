package org.example.javaprojektsystemrezerwacjihotelowej.service.pricing;

import org.example.javaprojektsystemrezerwacjihotelowej.entity.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DiscountPricingStrategyTest {

    private DiscountPricingStrategy pricingStrategy;
    private Room testRoom;

    @BeforeEach
    void setUp() {
        pricingStrategy = new DiscountPricingStrategy();
        
        // Set the discount properties manually since we're not using Spring context
        ReflectionTestUtils.setField(pricingStrategy, "discountThresholdDays", 3);
        ReflectionTestUtils.setField(pricingStrategy, "discountPercentage", 10);
        ReflectionTestUtils.setField(pricingStrategy, "additionalDiscountPercentage", 5);
        ReflectionTestUtils.setField(pricingStrategy, "maxDiscountPercentage", 25);
        
        // Create test room
        testRoom = new Room();
        testRoom.setRoomId(1L);
        testRoom.setRoomNumber("101");
        testRoom.setPrice(100.0);
    }

    @Test
    void calculatePrice_ForShortStay_ShouldNotApplyDiscount() {
        // Arrange
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = checkIn.plusDays(2); // 2 days, below threshold
        
        // Act
        BigDecimal price = pricingStrategy.calculatePrice(testRoom, checkIn, checkOut);
        
        // Assert
        assertEquals(new BigDecimal("200.0"), price);
    }

    @Test
    void calculatePrice_ForThresholdStay_ShouldApplyBaseDiscount() {
        // Arrange
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = checkIn.plusDays(3); // 3 days, at threshold
        
        // Expected: 300 - (300 * 10%) = 300 - 30 = 270
        BigDecimal expected = new BigDecimal("300.0")
                .subtract(new BigDecimal("300.0")
                        .multiply(new BigDecimal("0.1"))
                        .setScale(2, RoundingMode.HALF_UP));
        
        // Act
        BigDecimal price = pricingStrategy.calculatePrice(testRoom, checkIn, checkOut);
        
        // Assert
        assertEquals(expected, price);
    }

    @Test
    void calculatePrice_ForLongerStay_ShouldApplyIncreasedDiscount() {
        // Arrange
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = checkIn.plusDays(5); // 5 days, above threshold
        
        // Expected discount: 10% + (5-3)*5% = 10% + 10% = 20%
        // Expected: 500 - (500 * 20%) = 500 - 100 = 400
        BigDecimal expected = new BigDecimal("500.0")
                .subtract(new BigDecimal("500.0")
                        .multiply(new BigDecimal("0.2"))
                        .setScale(2, RoundingMode.HALF_UP));
        
        // Act
        BigDecimal price = pricingStrategy.calculatePrice(testRoom, checkIn, checkOut);
        
        // Assert
        assertEquals(expected, price);
    }

    @Test
    void calculatePrice_ForVeryLongStay_ShouldApplyMaxDiscount() {
        // Arrange
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = checkIn.plusDays(10); // 10 days, way above threshold
        
        // Expected discount: 10% + (10-3)*5% = 10% + 35% = 45%, but max is 25%
        // Expected: 1000 - (1000 * 25%) = 1000 - 250 = 750
        BigDecimal expected = new BigDecimal("1000.0")
                .subtract(new BigDecimal("1000.0")
                        .multiply(new BigDecimal("0.25"))
                        .setScale(2, RoundingMode.HALF_UP));
        
        // Act
        BigDecimal price = pricingStrategy.calculatePrice(testRoom, checkIn, checkOut);
        
        // Assert
        assertEquals(expected, price);
    }

    @Test
    void calculatePrice_ForSameDayCheckInAndCheckOut_ShouldChargeForOneDay() {
        // Arrange
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = checkIn; // Same day
        
        // Act
        BigDecimal price = pricingStrategy.calculatePrice(testRoom, checkIn, checkOut);
        
        // Assert
        assertEquals(new BigDecimal("100.0"), price);
    }

    @Test
    void getStrategyName_ShouldReturnDiscount() {
        // Act
        String name = pricingStrategy.getStrategyName();
        
        // Assert
        assertEquals("Discount", name);
    }
}