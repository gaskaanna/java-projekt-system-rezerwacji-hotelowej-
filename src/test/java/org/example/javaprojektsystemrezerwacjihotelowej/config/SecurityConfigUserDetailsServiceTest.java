package org.example.javaprojektsystemrezerwacjihotelowej.config;

import org.example.javaprojektsystemrezerwacjihotelowej.entity.Role;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.RoleName;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.User;
import org.example.javaprojektsystemrezerwacjihotelowej.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SecurityConfig securityConfig;

    private User testUser;
    private Set<Role> roles;

    @BeforeEach
    void setUp() {
        // Create test roles
        roles = new HashSet<>();
        Role userRole = new Role();
        userRole.setId(1);
        userRole.setName(RoleName.USER);
        
        Role adminRole = new Role();
        adminRole.setId(2);
        adminRole.setName(RoleName.ADMIN);
        
        roles.add(userRole);
        roles.add(adminRole);

        // Create test user
        testUser = new User();
        testUser.setUser_id(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword("encoded_password");
        testUser.setRoles(roles);
    }

    @Test
    void userDetailsService_ShouldReturnUserDetailsWithCorrectAuthorities() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        
        // Act
        UserDetailsService userDetailsService = securityConfig.userDetailsService();
        UserDetails userDetails = userDetailsService.loadUserByUsername("test@example.com");
        
        // Assert
        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("encoded_password", userDetails.getPassword());
        
        // Check authorities
        Set<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        
        assertEquals(2, authorities.size());
        assertTrue(authorities.contains("ROLE_USER"));
        assertTrue(authorities.contains("ROLE_ADMIN"));
        
        // Verify repository was called
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void userDetailsService_WithNonExistentUser_ShouldThrowUsernameNotFoundException() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
        
        // Act & Assert
        UserDetailsService userDetailsService = securityConfig.userDetailsService();
        
        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("nonexistent@example.com");
        });
        
        assertEquals("User not found: nonexistent@example.com", exception.getMessage());
        
        // Verify repository was called
        verify(userRepository).findByEmail("nonexistent@example.com");
    }
}