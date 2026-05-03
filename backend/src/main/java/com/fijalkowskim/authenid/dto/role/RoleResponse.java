package com.fijalkowskim.authenid.dto.role;

import com.fijalkowskim.authenid.model.role.Role;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Read-only projection of a Role entity returned from admin API endpoints.
 */
public record RoleResponse(
        Long id,
        String name,
        String description,
        Set<String> permissions
) {
    /**
     * Maps a Role entity to a RoleResponse DTO.
     *
     * @param role the role entity to map
     * @return a populated RoleResponse
     */
    public static RoleResponse from(Role role) {
        Set<String> permissionNames = role.getPermissions().stream()
                .map(p -> p.getName())
                .collect(Collectors.toSet());
        return new RoleResponse(
                role.getId(),
                role.getName(),
                role.getDescription(),
                permissionNames
        );
    }
}
