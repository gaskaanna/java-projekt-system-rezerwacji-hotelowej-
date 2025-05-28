package org.example.javaprojektsystemrezerwacjihotelowej.controller;

import org.example.javaprojektsystemrezerwacjihotelowej.entity.Room;
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
    public List<Room> getAllRooms() {
        return roomService.getAllRooms();
    }

    @Operation(summary = "Create a new room", description = "Add a new room to the hotel system.")
    @PostMapping
    public ResponseEntity<Room> createRoom(@RequestBody Room room) {
        return ResponseEntity.ok(roomService.createRoom(room));
    }

    @Operation(summary = "Abdate an existing room", description = "Update the details of an existing room.")
    @PutMapping("/{id}")
    public ResponseEntity<Room> updateRoom(
            @PathVariable Long id,
            @RequestBody Room room
    ) {
        return ResponseEntity.ok(roomService.updateRoom(id, room));
    }

    @Operation(summary = "Delete a room", description = "Remove a room from the hotel system.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Find available rooms", description = "Retrieve a list of available rooms based on check-in and check-out dates and minimum number of beds.")
    @GetMapping("/available")
    public List<Room> findAvailable(
            @RequestParam LocalDate checkIn,
            @RequestParam LocalDate checkOut,
            @RequestParam(defaultValue = "1") int beds
    ) {
        return roomService.findAvailableRooms(checkIn, checkOut, beds);
    }



}