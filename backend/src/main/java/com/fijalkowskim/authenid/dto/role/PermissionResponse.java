package com.fijalkowskim.authenid.dto.role;

import com.fijalkowskim.authenid.model.role.Permission;

/**
 * Read-only projection of a Permission entity returned from admin API endpoints.
 */
public record PermissionResponse(
        Long id,
        String name,
        String description
) {
    /**
     * Maps a Permission entity to a PermissionResponse DTO.
     *
     * @param permission the permission entity to map
     * @return a populated PermissionResponse
     */
    public static PermissionResponse from(Permission permission) {
        return new PermissionResponse(
                permission.getId(),
                permission.getName(),
                permission.getDescription()
        );
    }
}
