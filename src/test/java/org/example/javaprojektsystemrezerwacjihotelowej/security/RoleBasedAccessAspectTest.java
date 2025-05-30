package org.example.javaprojektsystemrezerwacjihotelowej.security;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.Reservation;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.RoleName;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.User;
import org.example.javaprojektsystemrezerwacjihotelowej.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RoleBasedAccessAspectTest {

    @Mock
    private ReservationService reservationService;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private MethodSignature methodSignature;

    @Mock
    private Method method;

    @InjectMocks
    private RoleBasedAccessAspect aspect;

    private Reservation testReservation;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Clear security context
        SecurityContextHolder.clearContext();

        // Create test user
        testUser = new User();
        testUser.setUser_id(1L);
        testUser.setEmail("test@example.com");

        // Create test reservation
        testReservation = new Reservation();
        testReservation.setReservationId(1L);
        testReservation.setUser(testUser);

        // Mock method signature
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(method);

        // Mock reservation service
        when(reservationService.getReservationById(1L)).thenReturn(testReservation);
    }

    @Test
    void checkAccess_WithAdminRole_ShouldProceed() throws Throwable {
        // Arrange
        mockAuthenticationWithRole(RoleName.ADMIN);
        mockAnnotation(new RoleName[]{}, true, RoleBasedAccess.ResourceType.RESERVATION, new RoleBasedAccess.Operation[]{});
        when(joinPoint.proceed()).thenReturn("Success");

        // Act
        Object result = aspect.checkAccess(joinPoint);

        // Assert
        assertEquals("Success", result);
        verify(joinPoint).proceed();
    }

    @Test
    void checkAccess_WithAllowedRole_ShouldProceed() throws Throwable {
        // Arrange
        mockAuthenticationWithRole(RoleName.USER);
        mockAnnotation(new RoleName[]{RoleName.USER}, false, RoleBasedAccess.ResourceType.RESERVATION, new RoleBasedAccess.Operation[]{});
        when(joinPoint.proceed()).thenReturn("Success");

        // Act
        Object result = aspect.checkAccess(joinPoint);

        // Assert
        assertEquals("Success", result);
        verify(joinPoint).proceed();
    }

    @Test
    void checkAccess_WithManagerRoleForReservationView_ShouldProceed() throws Throwable {
        // Arrange
        mockAuthenticationWithRole(RoleName.MENAGER);
        mockAnnotation(new RoleName[]{}, false, RoleBasedAccess.ResourceType.RESERVATION, 
                new RoleBasedAccess.Operation[]{RoleBasedAccess.Operation.VIEW});
        when(joinPoint.proceed()).thenReturn("Success");

        // Act
        Object result = aspect.checkAccess(joinPoint);

        // Assert
        assertEquals("Success", result);
        verify(joinPoint).proceed();
    }

    @Test
    void checkAccess_WithUserRoleAndOwnership_ShouldProceed() throws Throwable {
        // Arrange
        mockAuthenticationWithRole(RoleName.USER);
        mockAnnotation(new RoleName[]{}, true, RoleBasedAccess.ResourceType.RESERVATION, new RoleBasedAccess.Operation[]{});
        when(joinPoint.getArgs()).thenReturn(new Object[]{1L});
        when(joinPoint.proceed()).thenReturn("Success");

        // Act
        Object result = aspect.checkAccess(joinPoint);

        // Assert
        assertEquals("Success", result);
        verify(joinPoint).proceed();
        verify(reservationService).getReservationById(1L);
    }

    @Test
    void checkAccess_WithUserRoleButNotOwner_ShouldThrowException() throws Throwable {
        // Arrange
        mockAuthenticationWithRole(RoleName.USER);
        mockAnnotation(new RoleName[]{}, true, RoleBasedAccess.ResourceType.RESERVATION, new RoleBasedAccess.Operation[]{});
        when(joinPoint.getArgs()).thenReturn(new Object[]{1L});
        
        // Change the user email to make ownership check fail
        testUser.setEmail("different@example.com");

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> aspect.checkAccess(joinPoint));
        verify(joinPoint, never()).proceed();
    }

    @Test
    void checkAccess_WithNoAuthentication_ShouldThrowException() throws Throwable {
        // Arrange
        SecurityContextHolder.clearContext();
        mockAnnotation(new RoleName[]{}, false, RoleBasedAccess.ResourceType.RESERVATION, new RoleBasedAccess.Operation[]{});

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> aspect.checkAccess(joinPoint));
        verify(joinPoint, never()).proceed();
    }

    private void mockAuthenticationWithRole(RoleName roleName) {
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + roleName.name()));
        
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "test@example.com", "password", authorities);
        
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private void mockAnnotation(RoleName[] allowedRoles, boolean checkOwnership, 
                               RoleBasedAccess.ResourceType resourceType,
                               RoleBasedAccess.Operation[] allowedOperations) {
        RoleBasedAccess annotation = mock(RoleBasedAccess.class);
        when(annotation.allowedRoles()).thenReturn(allowedRoles);
        when(annotation.checkOwnership()).thenReturn(checkOwnership);
        when(annotation.resourceType()).thenReturn(resourceType);
        when(annotation.allowedOperations()).thenReturn(allowedOperations);
        
        when(method.getAnnotation(RoleBasedAccess.class)).thenReturn(annotation);
    }
}