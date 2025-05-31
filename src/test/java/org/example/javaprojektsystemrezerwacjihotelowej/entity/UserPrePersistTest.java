package org.example.javaprojektsystemrezerwacjihotelowej.entity;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserPrePersistTest {

    @Test
    void prePersist_ShouldSetCreatedAtField() throws Exception {
        // Arrange
        User user = new User();

        // Act - directly invoke the prePersist method using reflection
        Method prePersistMethod = User.class.getDeclaredMethod("prePersist");
        prePersistMethod.setAccessible(true);
        prePersistMethod.invoke(user);

        // Assert
        assertNotNull(user.getCreatedAt());
        assertTrue(user.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(user.getCreatedAt().isAfter(LocalDateTime.now().minusMinutes(1)));
    }
}
