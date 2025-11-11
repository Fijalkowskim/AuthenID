package com.fijalkowskim.authenid.exception;

import lombok.Getter;

@Getter
public class OAuthClientAlreadyExistsException extends RuntimeException {

    private final String clientId;

    public OAuthClientAlreadyExistsException(String clientId) {
        super("Client with id " + clientId + " already exists");
        this.clientId = clientId;
    }

}
