package com.fijalkowskim.authenid.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fijalkowskim.authenid.dto.client.OAuthClientCreateRequest;
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
 * Integration tests for {@link OAuthClientAdminController}.
 * Uses H2 in-memory database (test profile).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OAuthClientAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void getAllClients_shouldReturn200WithList() throws Exception {
        mockMvc.perform(get("/api/admin/clients"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void createClient_shouldReturn200WithSecretResponse() throws Exception {
        OAuthClientCreateRequest request = new OAuthClientCreateRequest(
                "test-client",
                "Test Client",
                Set.of("http://localhost:8080/callback"),
                Set.of("openid")
        );

        mockMvc.perform(post("/api/admin/clients")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").value("test-client"))
                .andExpect(jsonPath("$.clientSecret").isNotEmpty());
    }

    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void createClient_shouldReturn409WhenClientIdAlreadyExists() throws Exception {
        OAuthClientCreateRequest request = new OAuthClientCreateRequest(
                "demo-client", "Demo", Set.of("http://localhost:9000/callback"), Set.of("openid"));

        mockMvc.perform(post("/api/admin/clients")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void getClient_shouldReturn200ForExistingClient() throws Exception {
        mockMvc.perform(get("/api/admin/clients/demo-client"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").value("demo-client"));
    }

    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void getClient_shouldReturn404ForMissingClient() throws Exception {
        mockMvc.perform(get("/api/admin/clients/nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void rotateSecret_shouldReturn200WithNewSecret() throws Exception {
        mockMvc.perform(post("/api/admin/clients/demo-client/rotate-secret")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").value("demo-client"))
                .andExpect(jsonPath("$.clientSecret").isNotEmpty());
    }

    @Test
    void getAllClients_shouldReturn401WhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/admin/clients"))
                .andExpect(status().isUnauthorized());
    }
}
