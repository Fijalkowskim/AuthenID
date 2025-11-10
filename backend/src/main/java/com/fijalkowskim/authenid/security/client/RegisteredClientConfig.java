package com.fijalkowskim.authenid.security.client;

import com.fijalkowskim.authenid.repository.client.OAuthClientRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

/**
 * Configuration exposing RegisteredClientRepository backed by JPA.
 */
@Configuration
public class RegisteredClientConfig {

    @Bean
    public RegisteredClientRepository registeredClientRepository(OAuthClientRepository oauthClientRepository) {
        return new JpaRegisteredClientRepositoryAdapter(oauthClientRepository);
    }
}
