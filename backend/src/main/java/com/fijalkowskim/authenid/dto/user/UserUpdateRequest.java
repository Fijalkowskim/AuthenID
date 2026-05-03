package com.fijalkowskim.authenid.dto.user;

import com.fijalkowskim.authenid.model.user.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

/**
 * Request body for updating an existing user via the admin API.
 * All fields are required; partial updates are not supported to keep the API simple.
 */
public record UserUpdateRequest(

        @NotBlank
        @Size(max = 100)
        String username,

        @NotBlank
        @Email
        @Size(max = 190)
        String email,

        @Size(max = 40)
        String phoneNumber,

        boolean emailVerified,

        @NotNull
        UserStatus status,

        /** Role names to assign (replaces current roles). */
        Set<String> roleNames
) {}
