package com.fijalkowskim.authenid.model.client;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * OAuth 2.1 / OpenID Connect client entity persisted via JPA.
 * Stores core client configuration used by the authorization server.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "oauth_client", indexes = {
        @Index(name = "uk_oauth_client_client_id", columnList = "clientId", unique = true)
})
public class OAuthClient {

    @Id
    @Column(length = 100, nullable = false, updatable = false)
    private String id;

    @Column(nullable = false, unique = true, length = 100)
    private String clientId;

    @Column(nullable = false, length = 200)
    private String clientSecret;

    @Column(nullable = false, length = 200)
    private String clientName;

    @Column(nullable = false, length = 1000)
    private String clientAuthenticationMethods;

    @Column(nullable = false, length = 1000)
    private String authorizationGrantTypes;

    @Column(length = 1000)
    private String redirectUris;

    @Column(length = 1000)
    private String postLogoutRedirectUris;

    @Column(nullable = false, length = 1000)
    private String scopes;

    @Builder.Default
    @Column(nullable = false)
    private boolean requireProofKey = true;
}
