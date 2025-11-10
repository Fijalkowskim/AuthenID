package com.fijalkowskim.authenid.repository.client;

import com.fijalkowskim.authenid.model.client.OAuthClient;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OAuthClientRepository extends JpaRepository<OAuthClient, String> {

    Optional<OAuthClient> findByClientId(String clientId);
}
