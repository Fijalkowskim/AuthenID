package com.fijalkowskim.authenid.service.client;

import com.fijalkowskim.authenid.dto.client.OAuthClientCreateRequest;
import com.fijalkowskim.authenid.dto.client.OAuthClientResponse;
import com.fijalkowskim.authenid.dto.client.OAuthClientSecretResponse;
import com.fijalkowskim.authenid.exception.OAuthClientAlreadyExistsException;
import com.fijalkowskim.authenid.exception.OAuthClientNotFoundException;
import com.fijalkowskim.authenid.model.client.OAuthClient;
import com.fijalkowskim.authenid.repository.client.OAuthClientRepository;
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

/**
 * Admin service for managing OAuth 2.1 / OpenID Connect clients.
 * Provides create, read, list, and secret-rotation operations.
 */
@Service
@Transactional
public class OAuthClientAdminService {

    private final RegisteredClientRepository registeredClientRepository;
    private final OAuthClientRepository oAuthClientRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Constructs the service with required dependencies.
     *
     * @param registeredClientRepository Spring Authorization Server client repository
     * @param oAuthClientRepository      JPA repository for raw client entities
     * @param passwordEncoder            encoder used to hash client secrets
     */
    public OAuthClientAdminService(RegisteredClientRepository registeredClientRepository,
                                   OAuthClientRepository oAuthClientRepository,
                                   PasswordEncoder passwordEncoder) {
        this.registeredClientRepository = registeredClientRepository;
        this.oAuthClientRepository = oAuthClientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates a new OAuth client and returns the generated plain-text secret (shown only once).
     *
     * @param request client creation parameters
     * @return response containing client details and the raw client secret
     * @throws OAuthClientAlreadyExistsException if a client with the given clientId already exists
     */
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

    /**
     * Returns a list of all registered clients.
     *
     * @return list of client response DTOs
     */
    @Transactional(readOnly = true)
    public List<OAuthClientResponse> getAllClients() {
        return oAuthClientRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Returns details for a single client by clientId.
     *
     * @param clientId the public client identifier
     * @return client response DTO
     * @throws OAuthClientNotFoundException if no client with the given clientId exists
     */
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

    /**
     * Rotates the client secret for the given clientId.
     * The new plain-text secret is returned once and cannot be retrieved again.
     *
     * @param clientId the public client identifier
     * @return response containing the new raw secret
     * @throws OAuthClientNotFoundException if no client with the given clientId exists
     */
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

    private OAuthClientResponse toResponse(OAuthClient entity) {
        RegisteredClient client = registeredClientRepository.findByClientId(entity.getClientId());
        if (client == null) {
            return new OAuthClientResponse(entity.getClientId(), entity.getClientName(),
                    Set.of(), Set.of(), entity.isRequireProofKey());
        }
        return new OAuthClientResponse(
                client.getClientId(),
                client.getClientName(),
                client.getRedirectUris(),
                client.getScopes(),
                client.getClientSettings().isRequireProofKey()
        );
    }

    private String generateClientSecret() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}

