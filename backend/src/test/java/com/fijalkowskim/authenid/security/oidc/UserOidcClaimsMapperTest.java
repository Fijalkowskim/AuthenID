package com.fijalkowskim.authenid.security.oidc;

import com.fijalkowskim.authenid.model.role.Role;
import com.fijalkowskim.authenid.model.user.User;
import com.fijalkowskim.authenid.model.user.UserStatus;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserOidcClaimsMapperTest {

    private static final String USERNAME = "admin";
    private static final String EMAIL = "admin@authenid.local";
    private static final String ROLE_SYSTEM_ADMIN = "SYSTEM_ADMIN";
    private static final String ROLE_USER = "USER";

    private final UserOidcClaimsMapper mapper = new UserOidcClaimsMapper();

    @Test
    void buildIdTokenClaims_shouldIncludeStandardAndCustomClaims() {
        User user = new User();
        user.setUsername(USERNAME);
        user.setEmail(EMAIL);
        user.setStatus(UserStatus.ACTIVE);
        user.setRoles(Set.of(
                role(1L, ROLE_SYSTEM_ADMIN),
                role(2L, ROLE_USER)
        ));

        Map<String, Object> claims = mapper.buildIdTokenClaims(user);

        assertThat(claims.get("preferred_username")).isEqualTo(USERNAME);
        assertThat(claims.get("email")).isEqualTo(EMAIL);
        assertThat(claims.get("email_verified")).isEqualTo(Boolean.FALSE);

        Object roles = claims.get("roles");
        assertThat(roles).isInstanceOf(HashSet.class);

        @SuppressWarnings("unchecked")
        HashSet<String> rolesList = (HashSet<String>) roles;

        assertThat(rolesList).contains(ROLE_SYSTEM_ADMIN, ROLE_USER);
    }

    private Role role(Long id, String name) {
        Role role = new Role();
        role.setId(id);
        role.setName(name);
        return role;
    }
}
