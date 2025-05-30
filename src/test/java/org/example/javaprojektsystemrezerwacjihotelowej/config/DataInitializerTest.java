package org.example.javaprojektsystemrezerwacjihotelowej.config;

import org.example.javaprojektsystemrezerwacjihotelowej.entity.Role;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.RoleName;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.User;
import org.example.javaprojektsystemrezerwacjihotelowej.repository.RoleRepository;
import org.example.javaprojektsystemrezerwacjihotelowej.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DataInitializerTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private DataInitializer dataInitializer;

    private Role adminRole;
    private Role userRole;
    private Role menagerRole;

    @BeforeEach
    void setUp() {
        // Create roles
        adminRole = Role.builder().id(1).name(RoleName.ADMIN).build();
        userRole = Role.builder().id(2).name(RoleName.USER).build();
        menagerRole = Role.builder().id(3).name(RoleName.MENAGER).build();

        // Mock role repository
        when(roleRepository.findByName(RoleName.ADMIN)).thenReturn(Optional.of(adminRole));
        when(roleRepository.findByName(RoleName.USER)).thenReturn(Optional.empty());
        when(roleRepository.findByName(RoleName.MENAGER)).thenReturn(Optional.empty());
        when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> {
            Role role = invocation.getArgument(0);
            if (role.getName() == RoleName.USER) {
                return userRole;
            } else if (role.getName() == RoleName.MENAGER) {
                return menagerRole;
            }
            return role;
        });

        // Mock password encoder
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
    }

    @Test
    void seedDatabase_ShouldCreateRolesIfNotExist() throws Exception {
        // Arrange
        CommandLineRunner runner = dataInitializer.seedDatabase();

        // Act
        runner.run();

        // Assert
        verify(roleRepository, times(4)).findByName(any(RoleName.class));
        verify(roleRepository, times(2)).save(any(Role.class));
        verify(roleRepository, never()).save(argThat(role -> role.getName() == RoleName.ADMIN));
    }

    @Test
    void seedDatabase_ShouldCreateAdminUserIfNotExist() throws Exception {
        // Arrange
        when(userRepository.existsByEmail("admin@example.com")).thenReturn(false);
        CommandLineRunner runner = dataInitializer.seedDatabase();

        // Act
        runner.run();

        // Assert
        verify(userRepository).existsByEmail("admin@example.com");
        verify(passwordEncoder).encode("admin123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void seedDatabase_ShouldNotCreateAdminUserIfExists() throws Exception {
        // Arrange
        when(userRepository.existsByEmail("admin@example.com")).thenReturn(true);
        CommandLineRunner runner = dataInitializer.seedDatabase();

        // Act
        runner.run();

        // Assert
        verify(userRepository).existsByEmail("admin@example.com");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void seedDatabase_ShouldCreateUserWithCorrectProperties() throws Exception {
        // Arrange
        when(userRepository.existsByEmail("admin@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return user;
        });

        CommandLineRunner runner = dataInitializer.seedDatabase();

        // Act
        runner.run();

        // Assert
        verify(userRepository).save(argThat(user -> 
            user.getEmail().equals("admin@example.com") &&
            user.getPassword().equals("encoded_password") &&
            user.getRoles().contains(adminRole)
        ));
    }
}
