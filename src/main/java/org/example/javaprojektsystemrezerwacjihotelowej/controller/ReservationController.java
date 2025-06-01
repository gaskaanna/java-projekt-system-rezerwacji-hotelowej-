package org.example.javaprojektsystemrezerwacjihotelowej.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.javaprojektsystemrezerwacjihotelowej.dto.ReservationDTO;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.Reservation;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.RoleName;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.User;
import org.example.javaprojektsystemrezerwacjihotelowej.repository.UserRepository;
import org.example.javaprojektsystemrezerwacjihotelowej.security.RoleBasedAccess;
import org.example.javaprojektsystemrezerwacjihotelowej.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import org.example.javaprojektsystemrezerwacjihotelowej.entity.Room;
import org.example.javaprojektsystemrezerwacjihotelowej.service.RoomService;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Reservation Management", description = "Operations related to reservation management")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final RoomService roomService;
    private final UserRepository userRepository;

    @Operation(summary = "Get all reservations", description = "Retrieve a list of all reservations.")
    @GetMapping
    @RoleBasedAccess(
        allowedRoles = {RoleName.ADMIN, RoleName.MENAGER},
        allowedOperations = {RoleBasedAccess.Operation.VIEW}
    )
    public List<Reservation> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @Operation(summary = "Create a new reservation", description = "Add a new reservation to the hotel system.")
    @PostMapping
    @RoleBasedAccess(
        allowedRoles = {RoleName.ADMIN, RoleName.USER},
        allowedOperations = {RoleBasedAccess.Operation.CREATE}
    )
    public ResponseEntity<Reservation> createReservation (
            @RequestBody ReservationDTO reservationDTO,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userRepository.findById(reservationDTO.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found"));

        if (userDetails != null) {
            boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + RoleName.ADMIN.name()));

            if (!isAdmin && !user.getEmail().equals(userDetails.getUsername())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only create reservations for your own account");
            }
        }

        Room room = roomService.getRoomById(reservationDTO.roomId());

        if (room == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Room not found");
        }

        Reservation reservation = new Reservation();
        reservation.setCheckInDate(reservationDTO.checkInDate());
        reservation.setCheckOutDate(reservationDTO.checkOutDate());
        reservation.setSpecialRequests(reservationDTO.specialRequests());
        reservation.setUser(user);
        reservation.setRoom(room);

        return ResponseEntity.ok(reservationService.createReservation(reservation));
    }

    @Operation(summary = "Update an existing reservation", description = "Update the details of an existing reservation.")
    @PutMapping("/{id}")
    @RoleBasedAccess(
        allowedRoles = {RoleName.ADMIN},
        checkOwnership = true,
        resourceType = RoleBasedAccess.ResourceType.RESERVATION,
        allowedOperations = {RoleBasedAccess.Operation.UPDATE}
    )
    public ResponseEntity<Reservation> updateReservation(
            @PathVariable Long id,
            @RequestBody Reservation reservation,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // Additional check to ensure USER can only update their own reservations
        if (userDetails != null) {
            Reservation existingReservation = reservationService.getReservationById(id);
            boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + RoleName.ADMIN.name()));

            if (!isAdmin && !existingReservation.getUser().getEmail().equals(userDetails.getUsername())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only update your own reservations");
            }
        }

        return ResponseEntity.ok(reservationService.updateReservation(id, reservation));
    }

    @Operation(summary = "Cancel a reservation", description = "Cancel an existing reservation.")
    @DeleteMapping("/{id}")
    @RoleBasedAccess(
        allowedRoles = {RoleName.ADMIN, RoleName.MENAGER},
        checkOwnership = true,
        resourceType = RoleBasedAccess.ResourceType.RESERVATION,
        allowedOperations = {RoleBasedAccess.Operation.CANCEL}
    )
    public ResponseEntity<Void> cancelReservation(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // Additional check to ensure USER can only cancel their own reservations
        if (userDetails != null) {
            Reservation existingReservation = reservationService.getReservationById(id);
            boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + RoleName.ADMIN.name()));
            boolean isManager = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + RoleName.MENAGER.name()));

            if (!isAdmin && !isManager && !existingReservation.getUser().getEmail().equals(userDetails.getUsername())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only cancel your own reservations");
            }
        }

        reservationService.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }
}
