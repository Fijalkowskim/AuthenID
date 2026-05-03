package com.fijalkowskim.b2bshop.config;

import com.fijalkowskim.b2bshop.model.UserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.List;

public class UserInfoHelper {

    @SuppressWarnings("unchecked")
    public static UserInfo fromOidcUser(OidcUser oidcUser) {
        List<String> roles = oidcUser.getClaim("roles");
        return new UserInfo(
            oidcUser.getPreferredUsername(),
            oidcUser.getEmail(),
            roles != null ? roles : List.of()
        );
    }
}
