package com.fijalkowskim.authenid.security.client;

import com.fijalkowskim.authenid.model.client.OAuthClient;
import com.fijalkowskim.authenid.repository.client.OAuthClientRepository;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * RegisteredClientRepository implementation backed by JPA entity OAuthClient.
 */
public class JpaRegisteredClientRepositoryAdapter implements RegisteredClientRepository {

    private static final String DELIMITER = ",";

    private final OAuthClientRepository oauthClientRepository;

    public JpaRegisteredClientRepositoryAdapter(OAuthClientRepository oauthClientRepository) {
        this.oauthClientRepository = oauthClientRepository;
    }

    /**
     * Saves or updates a registered client using OAuthClient JPA entity.
     */
    @Override
    @Transactional
    public void save(RegisteredClient registeredClient) {
        OAuthClient entity = oauthClientRepository.findById(registeredClient.getId())
                .orElseGet(OAuthClient::new);

        entity.setId(registeredClient.getId());
        entity.setClientId(registeredClient.getClientId());
        entity.setClientSecret(registeredClient.getClientSecret());
        entity.setClientName(registeredClient.getClientName());
        entity.setClientAuthenticationMethods(joinAuthenticationMethods(registeredClient));
        entity.setAuthorizationGrantTypes(joinGrantTypes(registeredClient));
        entity.setRedirectUris(join(registeredClient.getRedirectUris()));
        entity.setPostLogoutRedirectUris(join(registeredClient.getPostLogoutRedirectUris()));
        entity.setScopes(join(registeredClient.getScopes()));
        entity.setRequireProofKey(registeredClient.getClientSettings().isRequireProofKey());

        oauthClientRepository.save(entity);
    }

    /**
     * Finds a registered client by internal identifier.
     */
    @Override
    @Transactional(readOnly = true)
    public RegisteredClient findById(String id) {
        if (!StringUtils.hasText(id)) {
            return null;
        }
        return oauthClientRepository.findById(id)
                .map(this::toRegisteredClient)
                .orElse(null);
    }

    /**
     * Finds a registered client by public client identifier.
     */
    @Override
    @Transactional(readOnly = true)
    public RegisteredClient findByClientId(String clientId) {
        if (!StringUtils.hasText(clientId)) {
            return null;
        }
        return oauthClientRepository.findByClientId(clientId)
                .map(this::toRegisteredClient)
                .orElse(null);
    }

    private RegisteredClient toRegisteredClient(OAuthClient entity) {
        RegisteredClient.Builder builder = RegisteredClient.withId(entity.getId())
                .clientId(entity.getClientId())
                .clientSecret(entity.getClientSecret())
                .clientName(entity.getClientName());

        split(entity.getClientAuthenticationMethods()).stream()
                .map(ClientAuthenticationMethod::new)
                .forEach(builder::clientAuthenticationMethod);

        split(entity.getAuthorizationGrantTypes()).stream()
                .map(AuthorizationGrantType::new)
                .forEach(builder::authorizationGrantType);

        split(entity.getRedirectUris()).forEach(builder::redirectUri);
        split(entity.getPostLogoutRedirectUris()).forEach(builder::postLogoutRedirectUri);
        split(entity.getScopes()).forEach(builder::scope);

        ClientSettings clientSettings = ClientSettings.builder()
                .requireProofKey(entity.isRequireProofKey())
                .build();

        TokenSettings tokenSettings = TokenSettings.builder().build();

        builder.clientSettings(clientSettings);
        builder.tokenSettings(tokenSettings);

        return builder.build();
    }

    private String joinAuthenticationMethods(RegisteredClient client) {
        Set<String> values = client.getClientAuthenticationMethods()
                .stream()
                .map(ClientAuthenticationMethod::getValue)
                .collect(Collectors.toSet());
        return join(values);
    }

    private String joinGrantTypes(RegisteredClient client) {
        Set<String> values = client.getAuthorizationGrantTypes()
                .stream()
                .map(AuthorizationGrantType::getValue)
                .collect(Collectors.toSet());
        return join(values);
    }

    private String join(Collection<String> values) {
        if (values == null || values.isEmpty()) {
            return "";
        }
        return values.stream()
                .filter(StringUtils::hasText)
                .collect(Collectors.joining(DELIMITER));
    }

    private Set<String> split(String value) {
        if (!StringUtils.hasText(value)) {
            return Set.of();
        }
        return Stream.of(value.split(DELIMITER))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());
    }

    private Stream<String> toStream(Iterable<String> values) {
        if (values == null) {
            return Stream.empty();
        }
        return StreamSupport.stream(values.spliterator(), false);
    }
}
