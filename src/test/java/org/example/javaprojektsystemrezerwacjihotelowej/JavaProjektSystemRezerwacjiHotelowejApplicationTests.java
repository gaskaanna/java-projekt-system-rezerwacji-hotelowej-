package org.example.javaprojektsystemrezerwacjihotelowej;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.lang.annotation.Annotation;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JavaProjektSystemRezerwacjiHotelowejApplicationTests {

    @Test
    void contextLoads() {
        // This test verifies that the Spring application context loads successfully
    }

    @Test
    void mainMethodShouldNotThrowException() {
        // This test verifies that the main method can be called without errors
        assertDoesNotThrow(() -> {
            JavaProjektSystemRezerwacjiHotelowejApplication.main(new String[]{});
        });
    }

    @Test
    void applicationShouldHaveCorrectAnnotations() {
        // This test verifies that the application class has the correct annotations
        Class<?> appClass = JavaProjektSystemRezerwacjiHotelowejApplication.class;

        // Check SpringBootApplication annotation
        assertTrue(appClass.isAnnotationPresent(SpringBootApplication.class));

        // Check EntityScan annotation
        assertTrue(appClass.isAnnotationPresent(EntityScan.class));
        EntityScan entityScan = appClass.getAnnotation(EntityScan.class);
        assertArrayEquals(new String[]{"org.example.javaprojektsystemrezerwacjihotelowej.entity"}, 
                entityScan.value());

        // Check EnableJpaRepositories annotation
        assertTrue(appClass.isAnnotationPresent(EnableJpaRepositories.class));
        EnableJpaRepositories enableJpaRepositories = appClass.getAnnotation(EnableJpaRepositories.class);
        assertArrayEquals(new String[]{"org.example.javaprojektsystemrezerwacjihotelowej.repository"}, 
                enableJpaRepositories.value());
    }
}
