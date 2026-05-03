package com.fijalkowskim.authenid.dto.user;

import com.fijalkowskim.authenid.model.user.User;
import com.fijalkowskim.authenid.model.user.UserStatus;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Read-only projection of a User entity returned from admin API endpoints.
 * Excludes password hash and internal attributes.
 */
public record UserResponse(
        Long id,
        String username,
        String email,
        String phoneNumber,
        boolean emailVerified,
        UserStatus status,
        Instant createdAt,
        Instant updatedAt,
        Instant lastLoginAt,
        Set<String> roles
) {
    /**
     * Maps a User entity to a UserResponse DTO.
     *
     * @param user the user entity to map
     * @return a populated UserResponse
     */
    public static UserResponse from(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(r -> r.getName())
                .collect(Collectors.toSet());
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.isEmailVerified(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getLastLoginAt(),
                roleNames
        );
    }
}
