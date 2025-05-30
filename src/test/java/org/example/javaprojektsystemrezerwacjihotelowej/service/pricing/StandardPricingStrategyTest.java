package org.example.javaprojektsystemrezerwacjihotelowej.service.pricing;

import org.example.javaprojektsystemrezerwacjihotelowej.entity.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class StandardPricingStrategyTest {

    private StandardPricingStrategy pricingStrategy;
    private Room testRoom;

    @BeforeEach
    void setUp() {
        pricingStrategy = new StandardPricingStrategy();
        
        // Create test room
        testRoom = new Room();
        testRoom.setRoomId(1L);
        testRoom.setRoomNumber("101");
        testRoom.setPrice(100.0);
    }

    @Test
    void calculatePrice_ForMultipleDays_ShouldReturnCorrectPrice() {
        // Arrange
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = checkIn.plusDays(3);
        
        // Act
        BigDecimal price = pricingStrategy.calculatePrice(testRoom, checkIn, checkOut);
        
        // Assert
        assertEquals(new BigDecimal("300.0"), price);
    }

    @Test
    void calculatePrice_ForSingleDay_ShouldReturnRoomPrice() {
        // Arrange
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = checkIn.plusDays(1);
        
        // Act
        BigDecimal price = pricingStrategy.calculatePrice(testRoom, checkIn, checkOut);
        
        // Assert
        assertEquals(new BigDecimal("100.0"), price);
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
    void calculatePrice_ForNegativeDays_ShouldChargeForOneDay() {
        // Arrange
        LocalDate checkIn = LocalDate.now();
        LocalDate checkOut = checkIn.minusDays(1); // Check-out before check-in
        
        // Act
        BigDecimal price = pricingStrategy.calculatePrice(testRoom, checkIn, checkOut);
        
        // Assert
        assertEquals(new BigDecimal("100.0"), price);
    }

    @Test
    void getStrategyName_ShouldReturnStandard() {
        // Act
        String name = pricingStrategy.getStrategyName();
        
        // Assert
        assertEquals("Standard", name);
    }
}