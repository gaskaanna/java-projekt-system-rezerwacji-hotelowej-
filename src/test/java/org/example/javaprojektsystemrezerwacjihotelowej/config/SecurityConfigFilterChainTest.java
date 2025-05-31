package org.example.javaprojektsystemrezerwacjihotelowej.config;

import org.example.javaprojektsystemrezerwacjihotelowej.entity.Role;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.RoleName;
import org.example.javaprojektsystemrezerwacjihotelowej.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigFilterChainTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SecurityConfig securityConfig;

    @Test
    void asAuthority_ShouldReturnCorrectAuthority() {
        // Arrange
        Role role = new Role();
        role.setName(RoleName.ADMIN);

        // Act
        SimpleGrantedAuthority result = ReflectionTestUtils.invokeMethod(securityConfig, "asAuthority", role);

        // Assert
        assertNotNull(result);
        assertEquals("ROLE_ADMIN", result.getAuthority());
    }

    @Test
    void asAuthority_WithUserRole_ShouldReturnCorrectAuthority() {
        // Arrange
        Role role = new Role();
        role.setName(RoleName.USER);

        // Act
        SimpleGrantedAuthority result = ReflectionTestUtils.invokeMethod(securityConfig, "asAuthority", role);

        // Assert
        assertNotNull(result);
        assertEquals("ROLE_USER", result.getAuthority());
    }

    @Test
    void asAuthority_WithManagerRole_ShouldReturnCorrectAuthority() {
        // Arrange
        Role role = new Role();
        role.setName(RoleName.MENAGER);

        // Act
        SimpleGrantedAuthority result = ReflectionTestUtils.invokeMethod(securityConfig, "asAuthority", role);

        // Assert
        assertNotNull(result);
        assertEquals("ROLE_MENAGER", result.getAuthority());
    }
}
