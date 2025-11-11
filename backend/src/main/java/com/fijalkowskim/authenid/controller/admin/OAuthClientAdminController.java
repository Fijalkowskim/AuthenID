package com.fijalkowskim.authenid.controller.admin;

import com.fijalkowskim.authenid.dto.client.OAuthClientCreateRequest;
import com.fijalkowskim.authenid.dto.client.OAuthClientResponse;
import com.fijalkowskim.authenid.dto.client.OAuthClientSecretResponse;
import com.fijalkowskim.authenid.service.client.OAuthClientAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Administrative API for managing OAuth/OIDC clients.
 */
@RestController
@RequestMapping("/api/admin/clients")
@PreAuthorize("hasRole('SYSTEM_ADMIN')")
public class OAuthClientAdminController {

    private final OAuthClientAdminService clientAdminService;

    public OAuthClientAdminController(OAuthClientAdminService clientAdminService) {
        this.clientAdminService = clientAdminService;
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
