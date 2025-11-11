package com.fijalkowskim.authenid.exception;

import lombok.Getter;

@Getter
public class OAuthClientNotFoundException extends RuntimeException {

    private final String clientId;

    public OAuthClientNotFoundException(String clientId) {
        super("Client not found: " + clientId);
        this.clientId = clientId;
    }

}
