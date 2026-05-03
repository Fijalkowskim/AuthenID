package com.fijalkowskim.authenid.dto.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

/**
 * Request body for creating a new role via the admin API.
 */
public record RoleCreateRequest(

        @NotBlank
        @Size(max = 60)
        String name,

        @Size(max = 255)
        String description,

        /** Permission names to attach to this role. */
        Set<String> permissionNames
) {}
