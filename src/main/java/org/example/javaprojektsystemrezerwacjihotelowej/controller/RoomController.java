package org.example.javaprojektsystemrezerwacjihotelowej.controller;

import org.example.javaprojektsystemrezerwacjihotelowej.entity.Room;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.RoleName;
import org.example.javaprojektsystemrezerwacjihotelowej.security.RoleBasedAccess;
import org.example.javaprojektsystemrezerwacjihotelowej.service.RoomService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@Tag(name = "Room Management", description = "Operations related to room management")
@RequiredArgsConstructor

public class RoomController {

    private final RoomService roomService;

    @Operation(summary = "Get all rooms", description = "Retrieve a list of all available rooms.")
    @GetMapping
    @RoleBasedAccess(
        allowedRoles = {RoleName.ADMIN, RoleName.MENAGER, RoleName.USER},
        resourceType = RoleBasedAccess.ResourceType.ROOM,
        allowedOperations = {RoleBasedAccess.Operation.VIEW}
    )
    public List<Room> getAllRooms() {
        return roomService.getAllRooms();
    }

    @Operation(summary = "Get room by ID", description = "Retrieve a specific room by its ID.")
    @GetMapping("/{id}")
    @RoleBasedAccess(
        allowedRoles = {RoleName.ADMIN, RoleName.MENAGER, RoleName.USER},
        resourceType = RoleBasedAccess.ResourceType.ROOM,
        allowedOperations = {RoleBasedAccess.Operation.VIEW}
    )
    public ResponseEntity<Room> getRoomById(@PathVariable Long id) {
        Room room = roomService.getRoomById(id);
        return ResponseEntity.ok(room);
    }

    @Operation(summary = "Create a new room", description = "Add a new room to the hotel system.")
    @PostMapping
    @RoleBasedAccess(
        allowedRoles = {RoleName.ADMIN},
        resourceType = RoleBasedAccess.ResourceType.ROOM,
        allowedOperations = {RoleBasedAccess.Operation.CREATE}
    )
    public ResponseEntity<Room> createRoom(@RequestBody Room room) {
        return ResponseEntity.ok(roomService.createRoom(room));
    }

    @Operation(summary = "Update an existing room", description = "Update the details of an existing room.")
    @PutMapping("/{id}")
    @RoleBasedAccess(
        allowedRoles = {RoleName.ADMIN},
        resourceType = RoleBasedAccess.ResourceType.ROOM,
        allowedOperations = {RoleBasedAccess.Operation.UPDATE}
    )
    public ResponseEntity<Room> updateRoom(
            @PathVariable Long id,
            @RequestBody Room room
    ) {
        return ResponseEntity.ok(roomService.updateRoom(id, room));
    }

    @Operation(summary = "Delete a room", description = "Remove a room from the hotel system.")
    @DeleteMapping("/{id}")
    @RoleBasedAccess(
        allowedRoles = {RoleName.ADMIN},
        resourceType = RoleBasedAccess.ResourceType.ROOM,
        allowedOperations = {RoleBasedAccess.Operation.DELETE}
    )
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Find available rooms", description = "Retrieve a list of available rooms based on check-in and check-out dates and minimum number of beds.")
    @GetMapping("/available")
    @RoleBasedAccess(
        allowedRoles = {RoleName.ADMIN, RoleName.MENAGER, RoleName.USER},
        resourceType = RoleBasedAccess.ResourceType.ROOM,
        allowedOperations = {RoleBasedAccess.Operation.VIEW}
    )
    public List<Room> findAvailable(
            @RequestParam LocalDate checkIn,
            @RequestParam LocalDate checkOut,
            @RequestParam(defaultValue = "1") int beds
    ) {
        return roomService.findAvailableRooms(checkIn, checkOut, beds);
    }



}
