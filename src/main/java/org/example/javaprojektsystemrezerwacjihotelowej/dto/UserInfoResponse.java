package org.example.javaprojektsystemrezerwacjihotelowej.dto;

import java.util.Set;

public record UserInfoResponse(String username,
                               String email,
                               Set<String> roles,
                               String comment) {}