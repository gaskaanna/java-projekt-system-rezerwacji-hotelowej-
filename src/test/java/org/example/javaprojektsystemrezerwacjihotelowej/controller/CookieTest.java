package org.example.javaprojektsystemrezerwacjihotelowej.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CookieTest {

    @Test
    void testCookieMaxAgeFormat() {
        // Create a cookie with maxAge=0
        ResponseCookie cookie = ResponseCookie.from("testCookie", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();

        // Print the cookie string for debugging
        System.out.println("Cookie string: " + cookie.toString());

        // Verify the cookie string contains "Max-Age=0"
        assertTrue(cookie.toString().contains("Max-Age=0"));
    }
}