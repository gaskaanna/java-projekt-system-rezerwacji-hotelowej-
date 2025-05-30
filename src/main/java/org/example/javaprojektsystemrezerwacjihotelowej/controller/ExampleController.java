package org.example.javaprojektsystemrezerwacjihotelowej.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.Reservation;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.RoleName;
import org.example.javaprojektsystemrezerwacjihotelowej.security.RoleBasedAccess;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * Example controller demonstrating how to use the @RoleBasedAccess annotation.
 * This controller is for documentation purposes only and is not meant to be used in production.
 */
@RestController
@RequestMapping("/example")
@Tag(name = "Example", description = "Example endpoints demonstrating role-based access control")
@RequiredArgsConstructor
public class ExampleController {

    /**
     * Example endpoint that allows only ADMIN users to access.
     * This demonstrates the simplest use case of the @RoleBasedAccess annotation.
     */
    @GetMapping("/admin-only")
    @Operation(summary = "Admin only endpoint", description = "This endpoint can only be accessed by users with ADMIN role")
    @RoleBasedAccess(
        allowedRoles = {RoleName.ADMIN},
        allowedOperations = {RoleBasedAccess.Operation.VIEW}
    )
    public ResponseEntity<String> adminOnlyEndpoint() {
        return ResponseEntity.ok("This endpoint can only be accessed by ADMIN users");
    }

    /**
     * Example endpoint that allows ADMIN and MANAGER users to access.
     * This demonstrates how to specify multiple roles.
     */
    @GetMapping("/admin-or-manager")
    @Operation(summary = "Admin or manager endpoint", description = "This endpoint can be accessed by users with ADMIN or MANAGER role")
    @RoleBasedAccess(
        allowedRoles = {RoleName.ADMIN, RoleName.MENAGER},
        allowedOperations = {RoleBasedAccess.Operation.VIEW}
    )
    public ResponseEntity<String> adminOrManagerEndpoint() {
        return ResponseEntity.ok("This endpoint can be accessed by ADMIN or MANAGER users");
    }

    /**
     * Example endpoint that allows users to access their own resources.
     * This demonstrates how to use the checkOwnership parameter.
     */
    @GetMapping("/user-resource/{id}")
    @Operation(summary = "User resource endpoint", description = "This endpoint can be accessed by the owner of the resource or ADMIN users")
    @RoleBasedAccess(
        checkOwnership = true,
        resourceType = RoleBasedAccess.ResourceType.RESERVATION,
        allowedOperations = {RoleBasedAccess.Operation.VIEW}
    )
    public ResponseEntity<String> userResourceEndpoint(@PathVariable Long id) {
        return ResponseEntity.ok("This endpoint can be accessed by the owner of resource " + id + " or ADMIN users");
    }

    /**
     * Example endpoint that allows MANAGER users to perform specific operations.
     * This demonstrates how to use the allowedOperations parameter.
     */
    @PutMapping("/manager-confirm/{id}")
    @Operation(summary = "Manager confirm endpoint", description = "This endpoint allows MANAGER users to confirm a reservation")
    @RoleBasedAccess(
        allowedRoles = {RoleName.MENAGER},
        resourceType = RoleBasedAccess.ResourceType.RESERVATION,
        allowedOperations = {RoleBasedAccess.Operation.CONFIRM}
    )
    public ResponseEntity<String> managerConfirmEndpoint(@PathVariable Long id) {
        return ResponseEntity.ok("This endpoint allows MANAGER users to confirm reservation " + id);
    }

    /**
     * Example endpoint that combines multiple access control rules.
     * This demonstrates a more complex use case.
     */
    @DeleteMapping("/cancel-reservation/{id}")
    @Operation(summary = "Cancel reservation endpoint", description = "This endpoint allows users to cancel their own reservations, or ADMIN/MANAGER users to cancel any reservation")
    @RoleBasedAccess(
        allowedRoles = {RoleName.ADMIN, RoleName.MENAGER},
        checkOwnership = true,
        resourceType = RoleBasedAccess.ResourceType.RESERVATION,
        allowedOperations = {RoleBasedAccess.Operation.CANCEL}
    )
    public ResponseEntity<String> cancelReservationEndpoint(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok("This endpoint allows users to cancel their own reservations, or ADMIN/MANAGER users to cancel any reservation");
    }
}