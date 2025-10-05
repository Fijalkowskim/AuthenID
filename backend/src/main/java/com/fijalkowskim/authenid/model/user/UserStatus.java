package com.fijalkowskim.authenid.model.user;

/**
 * High-level lifecycle state of a user account.
 */
public enum UserStatus {
    ACTIVE,
    LOCKED,
    SUSPENDED,
    PENDING_VERIFICATION,
    DELETED
}
