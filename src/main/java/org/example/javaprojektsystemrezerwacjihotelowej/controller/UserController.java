package org.example.javaprojektsystemrezerwacjihotelowej.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.javaprojektsystemrezerwacjihotelowej.entity.User;
import org.example.javaprojektsystemrezerwacjihotelowej.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "Operations related to user management")
@RequiredArgsConstructor

public class UserController {

    private final UserService userService;

    @Operation(summary = "Get all users", description = "Retrieve a list of all registered users.")
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @Operation(summary = "Create a new user", description = "Register a new user in the system.")
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @Operation(summary = "Update an existing user", description = "Update the details of an existing user.")
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @RequestBody User user
    ) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    @Operation(summary = "Delete a user", description = "Remove a user from the system.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}