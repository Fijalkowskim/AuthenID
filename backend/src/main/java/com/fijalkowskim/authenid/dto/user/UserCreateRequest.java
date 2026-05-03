package com.fijalkowskim.authenid.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

/**
 * Request body for creating a new user via the admin API.
 * Password is provided in plain text and will be encoded by the service layer.
 */
public record UserCreateRequest(

        @NotBlank
        @Size(max = 100)
        String username,

        @NotBlank
        @Size(min = 8, max = 128)
        String password,

        @NotBlank
        @Email
        @Size(max = 190)
        String email,

        @Size(max = 40)
        String phoneNumber,

        /** Role names to assign (e.g. "USER", "SYSTEM_ADMIN"). */
        Set<String> roleNames
) {}
