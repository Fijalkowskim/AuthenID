package com.fijalkowskim.authenid.bootstrap;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.stereotype.Component;

/**
 * Bootstrap task that creates default OAuth clients for development and testing
 * if they do not already exist. Runs at order 40, after users are created.
 * <p>
 * Creates three clients:
 * <ul>
 *   <li>{@code demo-client} — general-purpose test client</li>
 *   <li>{@code admin-panel} — React admin panel using Authorization Code + PKCE</li>
 *   <li>{@code b2b-shop} — B2B demo shop using Authorization Code + PKCE</li>
 * </ul>
 * </p>
 */
@Component
@Order(40)
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "authenid.bootstrap.clients.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class ClientBootstrapTask implements BootstrapTask {

    private final RegisteredClientRepository registeredClientRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Initializes default OIDC clients for development and testing.
     */
    @Override
    public void run() {
        bootstrapDemoClient();
        bootstrapAdminPanelClient();
        bootstrapB2bShopClient();
    }

    private void bootstrapDemoClient() {
        if (registeredClientRepository.findByClientId("demo-client") != null) {
            return;
        }

        RegisteredClient demoClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("demo-client")
                .clientSecret(passwordEncoder.encode("secret"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("http://localhost:9000/callback")
                .scope(OidcScopes.OPENID)
                .clientSettings(ClientSettings.builder()
                        .requireProofKey(true)
                        .build())
                .build();

        registeredClientRepository.save(demoClient);
    }

    private void bootstrapAdminPanelClient() {
        if (registeredClientRepository.findByClientId("admin-panel") != null) {
            return;
        }

        RegisteredClient adminPanel = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("admin-panel")
                .clientSecret(passwordEncoder.encode("admin-panel-secret"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("http://localhost:3000/callback")
                .postLogoutRedirectUri("http://localhost:3000/")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope("email")
                .clientSettings(ClientSettings.builder()
                        .requireProofKey(true)
                        .build())
                .build();

        registeredClientRepository.save(adminPanel);
    }

    private void bootstrapB2bShopClient() {
        if (registeredClientRepository.findByClientId("b2b-shop") != null) {
            return;
        }

        RegisteredClient b2bShop = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("b2b-shop")
                .clientSecret(passwordEncoder.encode("b2b-shop-secret"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("http://localhost:8081/login/oauth2/code/authenid")
                .postLogoutRedirectUri("http://localhost:8081/")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope("email")
                .clientSettings(ClientSettings.builder()
                        .requireProofKey(false)
                        .build())
                .build();

        registeredClientRepository.save(b2bShop);
    }
}
