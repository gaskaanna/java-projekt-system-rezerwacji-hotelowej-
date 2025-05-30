package org.example.javaprojektsystemrezerwacjihotelowej.security;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.Reservation;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.RoleName;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.User;
import org.example.javaprojektsystemrezerwacjihotelowej.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

@Aspect
@Component
public class RoleBasedAccessAspect {

    private final ReservationService reservationService;

    public RoleBasedAccessAspect(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Around("@annotation(org.example.javaprojektsystemrezerwacjihotelowej.security.RoleBasedAccess)")
    public Object checkAccess(ProceedingJoinPoint joinPoint) throws Throwable {
        // Get the method signature
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // Get the annotation
        RoleBasedAccess annotation = method.getAnnotation(RoleBasedAccess.class);

        // Get the current authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Authentication required");
        }

        // Check if user has ADMIN role - admins can do anything
        if (hasRole(authentication, RoleName.ADMIN)) {
            return joinPoint.proceed();
        }

        // Check if user has any of the allowed roles
        boolean hasAllowedRole = false;
        for (RoleName role : annotation.allowedRoles()) {
            if (hasRole(authentication, role)) {
                hasAllowedRole = true;
                break;
            }
        }

        // If user has an allowed role, proceed
        if (hasAllowedRole) {
            return joinPoint.proceed();
        }

        // If no allowed roles specified, check if user has MANAGER role for reservations
        if (!hasAllowedRole && annotation.resourceType() == RoleBasedAccess.ResourceType.RESERVATION) {
            if (hasRole(authentication, RoleName.MENAGER)) {
                // Managers can view, confirm, and cancel reservations
                RoleBasedAccess.Operation[] operations = annotation.allowedOperations();
                if (operations.length == 0 || 
                    Arrays.asList(operations).contains(RoleBasedAccess.Operation.VIEW) ||
                    Arrays.asList(operations).contains(RoleBasedAccess.Operation.CONFIRM) ||
                    Arrays.asList(operations).contains(RoleBasedAccess.Operation.CANCEL)) {
                    return joinPoint.proceed();
                }
            }
        }

        // If ownership check is required and user has USER role
        if (annotation.checkOwnership() && hasRole(authentication, RoleName.USER)) {
            // Get the resource ID from method arguments
            Long resourceId = getResourceId(joinPoint);
            if (resourceId != null) {
                // Check if user is the owner of the resource
                if (isUserOwnerOfResource(authentication.getName(), resourceId, annotation.resourceType())) {
                    return joinPoint.proceed();
                }
            }
        }

        // If we get here, access is denied
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
    }

    private boolean hasRole(Authentication authentication, RoleName roleName) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return authorities.stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + roleName.name()));
    }

    private Long getResourceId(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof Long) {
                return (Long) arg;
            }
        }
        return null;
    }

    private boolean isUserOwnerOfResource(String username, Long resourceId, RoleBasedAccess.ResourceType resourceType) {
        if (resourceType == RoleBasedAccess.ResourceType.RESERVATION) {
            Reservation reservation = reservationService.getReservationById(resourceId);
            return reservation.getUser().getEmail().equals(username);
        }
        return false;
    }
}
