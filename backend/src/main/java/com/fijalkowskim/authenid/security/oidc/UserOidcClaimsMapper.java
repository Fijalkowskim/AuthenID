package com.fijalkowskim.authenid.security.oidc;

import com.fijalkowskim.authenid.model.role.Role;
import com.fijalkowskim.authenid.model.user.User;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Maps internal User entity to OpenID Connect claims for ID token and UserInfo.
 */
@Component
public class UserOidcClaimsMapper {

    public Map<String, Object> buildIdTokenClaims(User user) {
        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("sub", String.valueOf(user.getId()));
        claims.put("preferred_username", user.getUsername());
        claims.put("email", user.getEmail());
        claims.put("email_verified", user.isEmailVerified());
        claims.put("name", user.getUsername());
        claims.put("roles", extractRoleNames(user.getRoles()));
        return claims;
    }

    public Map<String, Object> buildUserInfoClaims(User user) {
        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("sub", String.valueOf(user.getId()));
        claims.put("preferred_username", user.getUsername());
        claims.put("email", user.getEmail());
        claims.put("email_verified", user.isEmailVerified());
        claims.put("name", user.getUsername());
        claims.put("roles", extractRoleNames(user.getRoles()));
        return claims;
    }

    private Set<String> extractRoleNames(Set<Role> roles) {
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }
}
