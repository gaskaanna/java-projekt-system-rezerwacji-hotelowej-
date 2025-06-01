package org.example.javaprojektsystemrezerwacjihotelowej.security;

import org.example.javaprojektsystemrezerwacjihotelowej.entity.RoleName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for the role-based access control.
 * These tests verify that the @RoleBasedAccess annotation is working correctly.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class RoleBasedAccessTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void adminOnly_WithAdminRole_ShouldBeAccessible() throws Exception {
        mockMvc.perform(get("/example/admin-only"))
                .andExpect(status().isOk())
                .andExpect(content().string("This endpoint can only be accessed by ADMIN users"));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    public void adminOrManager_WithAdminRole_ShouldBeAccessible() throws Exception {
        mockMvc.perform(get("/example/admin-or-manager"))
                .andExpect(status().isOk())
                .andExpect(content().string("This endpoint can be accessed by ADMIN or MANAGER users"));
    }

    @Test
    @WithMockUser(roles = "MENAGER")
    public void adminOrManager_WithManagerRole_ShouldBeAccessible() throws Exception {
        mockMvc.perform(get("/example/admin-or-manager"))
                .andExpect(status().isOk())
                .andExpect(content().string("This endpoint can be accessed by ADMIN or MANAGER users"));
    }











}