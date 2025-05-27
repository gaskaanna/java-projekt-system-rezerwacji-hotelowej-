package org.example.javaprojektsystemrezerwacjihotelowej.controller;

import org.example.javaprojektsystemrezerwacjihotelowej.entity.Room;
import org.example.javaprojektsystemrezerwacjihotelowej.repository.RoomsRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")

public class RoomController {

    @Autowired
    private RoomsRepository roomsRepository;

    @GetMapping
    public List<Room> getAllRooms() {
        return roomsRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Room> createRoom(@RequestBody Room room) {
        Room savedRoom = roomsRepository.save(room);
        return ResponseEntity.ok(savedRoom);
    }
}