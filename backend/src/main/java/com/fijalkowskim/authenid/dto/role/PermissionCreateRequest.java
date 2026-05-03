package com.fijalkowskim.authenid.dto.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request body for creating a new permission via the admin API.
 */
public record PermissionCreateRequest(

        @NotBlank
        @Size(max = 100)
        String name,

        @Size(max = 255)
        String description
) {}
