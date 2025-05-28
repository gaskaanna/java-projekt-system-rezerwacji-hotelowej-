package org.example.javaprojektsystemrezerwacjihotelowej.controller;

import org.example.javaprojektsystemrezerwacjihotelowej.entity.Room;
import org.example.javaprojektsystemrezerwacjihotelowej.repository.RoomsRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@Tag(name = "Room Management", description = "Operations related to room management")


public class RoomController {

    @Autowired
    private RoomsRepository roomsRepository;

    @Operation(summary = "Get all rooms", description = "Retrieve a list of all available rooms.")
    @GetMapping
    public List<Room> getAllRooms() {
        return roomsRepository.findAll();
    }

    @Operation(summary = "Create a new room", description = "Add a new room to the hotel system.")
    @PostMapping
    public ResponseEntity<Room> createRoom(
            @Parameter(description = "Room object to be created", required = true)
            @RequestBody Room room) {
        Room savedRoom = roomsRepository.save(room);
        return ResponseEntity.ok(savedRoom);
    }
}