package org.example.javaprojektsystemrezerwacjihotelowej.entity;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenPrePersistTest {

    @Test
    void prePersist_ShouldSetCreatedAtField() throws Exception {
        // Arrange
        RefreshToken refreshToken = new RefreshToken();

        // Act - directly invoke the prePersist method using reflection
        Method prePersistMethod = RefreshToken.class.getDeclaredMethod("prePersist");
        prePersistMethod.setAccessible(true);
        prePersistMethod.invoke(refreshToken);

        // Assert
        assertNotNull(refreshToken.getCreatedAt());
        assertTrue(refreshToken.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(refreshToken.getCreatedAt().isAfter(LocalDateTime.now().minusMinutes(1)));
    }
}
