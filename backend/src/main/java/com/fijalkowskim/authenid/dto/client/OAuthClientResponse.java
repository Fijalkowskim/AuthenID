package com.fijalkowskim.authenid.dto.client;

import java.util.Set;

/**
 * Representation of an OAuth/OIDC client for admin API responses.
 */
public record OAuthClientResponse(
        String clientId,
        String clientName,
        Set<String> redirectUris,
        Set<String> scopes,
        boolean requireProofKey
) {
}
