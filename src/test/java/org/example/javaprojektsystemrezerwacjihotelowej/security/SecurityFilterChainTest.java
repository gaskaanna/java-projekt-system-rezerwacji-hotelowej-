package org.example.javaprojektsystemrezerwacjihotelowej.security;

import org.example.javaprojektsystemrezerwacjihotelowej.config.SecurityConfig;
import org.example.javaprojektsystemrezerwacjihotelowej.filter.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for the security filter chain configuration.
 * These tests verify that the security rules are correctly applied.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class SecurityFilterChainTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    public void publicEndpoints_ShouldBeAccessibleWithoutAuthentication() throws Exception {
        // Test access to Swagger UI
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().isOk());

        // Test access to API docs
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());

        // Test access to auth endpoints
        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isOk());

        // Test access to example endpoints
        mockMvc.perform(get("/example/admin-only"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void adminEndpoints_WithAdminRole_ShouldBeAccessible() throws Exception {
        // Test access to admin endpoints with ADMIN role
        mockMvc.perform(get("/admin/test"))
                .andExpect(status().isOk());
    }



    @Test
    @WithMockUser(roles = "USER")
    public void protectedEndpoints_WithAuthentication_ShouldBeAccessible() throws Exception {
        // Test access to protected endpoints with authentication
        mockMvc.perform(get("/api/rooms"))
                .andExpect(status().isOk());
    }


}