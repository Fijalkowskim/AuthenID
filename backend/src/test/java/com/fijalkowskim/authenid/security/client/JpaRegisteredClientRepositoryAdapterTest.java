package com.fijalkowskim.authenid.security.client;

import com.fijalkowskim.authenid.model.client.OAuthClient;
import com.fijalkowskim.authenid.repository.client.OAuthClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link JpaRegisteredClientRepositoryAdapter}.
 * Verifies persistence and retrieval of registered clients via the JPA adapter.
 */
@SpringBootTest
@Transactional
class JpaRegisteredClientRepositoryAdapterTest {

    @Autowired
    private OAuthClientRepository oAuthClientRepository;

    private JpaRegisteredClientRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new JpaRegisteredClientRepositoryAdapter(oAuthClientRepository);
    }

    @Test
    void save_andFindByClientId_shouldRoundTrip() {
        RegisteredClient client = buildClient("adapter-test-client");

        adapter.save(client);

        RegisteredClient found = adapter.findByClientId("adapter-test-client");
        assertThat(found).isNotNull();
        assertThat(found.getClientId()).isEqualTo("adapter-test-client");
        assertThat(found.getClientName()).isEqualTo("Adapter Test");
        assertThat(found.getScopes()).contains("openid");
    }

    @Test
    void save_andFindById_shouldRoundTrip() {
        String id = UUID.randomUUID().toString();
        RegisteredClient client = RegisteredClient.withId(id)
                .clientId("by-id-client")
                .clientSecret("{noop}secret")
                .clientName("By ID Client")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://localhost/cb")
                .scope("openid")
                .clientSettings(ClientSettings.builder().requireProofKey(true).build())
                .build();

        adapter.save(client);

        RegisteredClient found = adapter.findById(id);
        assertThat(found).isNotNull();
        assertThat(found.getClientId()).isEqualTo("by-id-client");
    }

    @Test
    void findByClientId_shouldReturnNullWhenMissing() {
        assertThat(adapter.findByClientId("ghost-client")).isNull();
    }

    @Test
    void findById_shouldReturnNullWhenMissing() {
        assertThat(adapter.findById("nonexistent-id")).isNull();
    }

    @Test
    void save_shouldUpdateExistingClient() {
        RegisteredClient original = buildClient("updatable-client");
        adapter.save(original);

        RegisteredClient updated = RegisteredClient.from(original)
                .clientName("Updated Name")
                .build();
        adapter.save(updated);

        RegisteredClient found = adapter.findByClientId("updatable-client");
        assertThat(found).isNotNull();
        assertThat(found.getClientName()).isEqualTo("Updated Name");
    }

    @Test
    void save_shouldPersistPkceRequirement() {
        RegisteredClient client = buildClient("pkce-client");
        adapter.save(client);

        RegisteredClient found = adapter.findByClientId("pkce-client");
        assertThat(found).isNotNull();
        assertThat(found.getClientSettings().isRequireProofKey()).isTrue();
    }

    private RegisteredClient buildClient(String clientId) {
        return RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(clientId)
                .clientSecret("{noop}secret")
                .clientName("Adapter Test")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("http://localhost:9000/callback")
                .scope("openid")
                .clientSettings(ClientSettings.builder().requireProofKey(true).build())
                .build();
    }
}
