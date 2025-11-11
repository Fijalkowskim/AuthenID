package com.fijalkowskim.authenid.dto.client;

import java.util.Set;

/**
 * Request payload for creating a new OAuth/OIDC client.
 */
public record OAuthClientCreateRequest(
        String clientId,
        String clientName,
        Set<String> redirectUris,
        Set<String> scopes
) {
}
