package com.fijalkowskim.authenid.controller.admin;

import com.fijalkowskim.authenid.dto.client.OAuthClientCreateRequest;
import com.fijalkowskim.authenid.dto.client.OAuthClientResponse;
import com.fijalkowskim.authenid.dto.client.OAuthClientSecretResponse;
import com.fijalkowskim.authenid.service.client.OAuthClientAdminService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Administrative API for managing OAuth/OIDC clients.
 * All endpoints require the SYSTEM_ADMIN role.
 */
@RestController
@RequestMapping("/api/admin/clients")
@PreAuthorize("hasRole('SYSTEM_ADMIN')")
public class OAuthClientAdminController {

    private final OAuthClientAdminService clientAdminService;

    /**
     * Constructs the controller with required dependencies.
     *
     * @param clientAdminService service for OAuth client operations
     */
    public OAuthClientAdminController(OAuthClientAdminService clientAdminService) {
        this.clientAdminService = clientAdminService;
    }

    /**
     * Returns a list of all registered OAuth clients.
     *
     * @return list of client response DTOs
     */
    @GetMapping
    public ResponseEntity<List<OAuthClientResponse>> getAllClients() {
        return ResponseEntity.ok(clientAdminService.getAllClients());
    }

    @PostMapping
    public ResponseEntity<OAuthClientSecretResponse> createClient(@RequestBody OAuthClientCreateRequest request) {
        OAuthClientSecretResponse response = clientAdminService.createClient(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<OAuthClientResponse> getClient(@PathVariable String clientId) {
        OAuthClientResponse response = clientAdminService.getClient(clientId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{clientId}/rotate-secret")
    public ResponseEntity<OAuthClientSecretResponse> rotateSecret(@PathVariable String clientId) {
        OAuthClientSecretResponse response = clientAdminService.rotateSecret(clientId);
        return ResponseEntity.ok(response);
    }
}
