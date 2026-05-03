package com.fijalkowskim.b2bshop.model;

import java.util.List;

public record UserInfo(
    String username,
    String email,
    List<String> roles
) {
    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }

    public boolean isAdmin() {
        return hasRole("B2B_ADMIN");
    }

    public boolean isSales() {
        return hasRole("B2B_SALES");
    }

    public boolean isBuyer() {
        return hasRole("B2B_BUYER");
    }
}
