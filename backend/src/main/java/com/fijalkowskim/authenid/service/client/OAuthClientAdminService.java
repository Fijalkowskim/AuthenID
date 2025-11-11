package com.fijalkowskim.authenid.service.client;

import com.fijalkowskim.authenid.dto.client.OAuthClientCreateRequest;
import com.fijalkowskim.authenid.dto.client.OAuthClientResponse;
import com.fijalkowskim.authenid.dto.client.OAuthClientSecretResponse;
import com.fijalkowskim.authenid.exception.OAuthClientAlreadyExistsException;
import com.fijalkowskim.authenid.exception.OAuthClientNotFoundException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OAuthClientAdminService {

    private final RegisteredClientRepository registeredClientRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    public OAuthClientAdminService(RegisteredClientRepository registeredClientRepository,
                                   PasswordEncoder passwordEncoder) {
        this.registeredClientRepository = registeredClientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public OAuthClientSecretResponse createClient(OAuthClientCreateRequest request) {
        if (registeredClientRepository.findByClientId(request.clientId()) != null) {
            throw new OAuthClientAlreadyExistsException(request.clientId());
        }

        String rawSecret = generateClientSecret();

        RegisteredClient.Builder builder = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(request.clientId())
                .clientSecret(passwordEncoder.encode(rawSecret))
                .clientName(request.clientName())
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .scope(OidcScopes.OPENID)
                .clientSettings(ClientSettings.builder()
                        .requireProofKey(true)
                        .build());

        if (request.redirectUris() != null) {
            request.redirectUris().forEach(builder::redirectUri);
        }

        if (request.scopes() != null) {
            request.scopes().forEach(builder::scope);
        }

        RegisteredClient client = builder.build();
        registeredClientRepository.save(client);

        Set<String> scopes = client.getScopes();

        return new OAuthClientSecretResponse(
                client.getClientId(),
                rawSecret,
                client.getClientName(),
                client.getRedirectUris(),
                scopes
        );
    }

    @Transactional(readOnly = true)
    public List<OAuthClientResponse> getAllClients() {
        throw new UnsupportedOperationException("Listing all clients is not implemented for this repository");
    }

    @Transactional(readOnly = true)
    public OAuthClientResponse getClient(String clientId) {
        RegisteredClient client = registeredClientRepository.findByClientId(clientId);
        if (client == null) {
            throw new OAuthClientNotFoundException(clientId);
        }
        return new OAuthClientResponse(
                client.getClientId(),
                client.getClientName(),
                client.getRedirectUris(),
                client.getScopes(),
                client.getClientSettings().isRequireProofKey()
        );
    }

    public OAuthClientSecretResponse rotateSecret(String clientId) {
        RegisteredClient existing = registeredClientRepository.findByClientId(clientId);
        if (existing == null) {
            throw new OAuthClientNotFoundException(clientId);
        }

        String newRawSecret = generateClientSecret();

        RegisteredClient updated = RegisteredClient.from(existing)
                .clientSecret(passwordEncoder.encode(newRawSecret))
                .build();

        registeredClientRepository.save(updated);

        return new OAuthClientSecretResponse(
                updated.getClientId(),
                newRawSecret,
                updated.getClientName(),
                updated.getRedirectUris(),
                updated.getScopes()
        );
    }

    private String generateClientSecret() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
