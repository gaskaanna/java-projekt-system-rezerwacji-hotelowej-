package org.example.javaprojektsystemrezerwacjihotelowej.controller;

import io.swagger.v3.oas.annotations.Parameter;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.User;
import org.example.javaprojektsystemrezerwacjihotelowej.repository.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")

@Tag(name = "User Management", description = "Operations related to user management")

public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Operation(summary = "Get all users", description = "Retrieve a list of all registered users.")
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Operation(summary = "Create a new user", description = "Register a new user in the system.")
    @PostMapping
    public ResponseEntity<User> createUser(
            @Parameter(description = "User object to be created", required = true)
            @RequestBody User user) {
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }
}