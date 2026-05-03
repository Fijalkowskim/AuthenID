package com.fijalkowskim.authenid.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fijalkowskim.authenid.dto.user.UserCreateRequest;
import com.fijalkowskim.authenid.model.user.User;
import com.fijalkowskim.authenid.model.user.UserStatus;
import com.fijalkowskim.authenid.repository.role.RoleRepository;
import com.fijalkowskim.authenid.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for {@link UserAdminController}.
 * Uses H2 in-memory database (test profile) and Spring Security test support.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void getAllUsers_shouldReturn200WithList() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void createUser_shouldReturn201WithCreatedUser() throws Exception {
        UserCreateRequest request = new UserCreateRequest(
                "newuser", "password123", "newuser@example.com", null, Set.of());

        mockMvc.perform(post("/api/admin/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.email").value("newuser@example.com"));
    }

    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void createUser_shouldReturn400WhenInvalidRequest() throws Exception {
        UserCreateRequest invalid = new UserCreateRequest("", "pass", "not-an-email", null, Set.of());

        mockMvc.perform(post("/api/admin/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void getUser_shouldReturn404WhenNotFound() throws Exception {
        mockMvc.perform(get("/api/admin/users/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void getUser_shouldReturn200WhenExists() throws Exception {
        User saved = userRepository.save(User.builder()
                .username("findme")
                .passwordHash("hash")
                .email("findme@example.com")
                .status(UserStatus.ACTIVE)
                .build());

        mockMvc.perform(get("/api/admin/users/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("findme"));
    }

    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void deleteUser_shouldReturn204() throws Exception {
        User saved = userRepository.save(User.builder()
                .username("todelete")
                .passwordHash("hash")
                .email("todelete@example.com")
                .status(UserStatus.ACTIVE)
                .build());

        mockMvc.perform(delete("/api/admin/users/" + saved.getId())
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAllUsers_shouldReturn401WhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllUsers_shouldReturn403WhenNotAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isForbidden());
    }
}
