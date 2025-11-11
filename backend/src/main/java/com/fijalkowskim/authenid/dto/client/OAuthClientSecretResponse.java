package com.fijalkowskim.authenid.dto.client;

import java.util.Set;

/**
 * Response returned after creating or rotating a client secret.
 */
public record OAuthClientSecretResponse(
        String clientId,
        String clientSecret,
        String clientName,
        Set<String> redirectUris,
        Set<String> scopes
) {
}
