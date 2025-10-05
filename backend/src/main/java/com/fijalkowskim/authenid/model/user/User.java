package com.fijalkowskim.authenid.model.user;

import com.fijalkowskim.authenid.model.role.Permission;
import com.fijalkowskim.authenid.model.role.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Identity Provider user entity compatible with Spring Security's UserDetails.
 * Stores core attributes, status, timestamps and relationships to roles/permissions.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "`user`", indexes = {
        @Index(name = "uk_user_username", columnList = "username", unique = true),
        @Index(name = "uk_user_email", columnList = "email", unique = true)
})
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @NotBlank
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @NotBlank
    @Email
    @Size(max = 190)
    @Column(nullable = false, unique = true, length = 190)
    private String email;

    @Size(max = 40)
    @Column(length = 40)
    private String phoneNumber;

    @Builder.Default
    @Column(nullable = false)
    private boolean emailVerified = false;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private UserStatus status = UserStatus.ACTIVE;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    @Column
    private Instant lastLoginAt;

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, Object> attributes = new LinkedHashMap<>();

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "role_id", nullable = false),
            uniqueConstraints = @UniqueConstraint(name = "uk_user_role", columnNames = {"user_id", "role_id"}))
    private Set<Role> roles = new LinkedHashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<String> roleAuthorities = roles.stream()
                .map(Role::getName)
                .filter(Objects::nonNull)
                .map(name -> name.startsWith("ROLE_") ? name : "ROLE_" + name)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Set<String> permAuthorities = roles.stream()
                .flatMap(r -> Optional.ofNullable(r.getPermissions()).orElseGet(Set::of).stream())
                .map(Permission::getName)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        return concatAuthorities(roleAuthorities, permAuthorities).stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    private static Set<String> concatAuthorities(Set<String> roles, Set<String> perms) {
        Set<String> out = new LinkedHashSet<>(roles.size() + perms.size());
        out.addAll(roles);
        out.addAll(perms);
        return out;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return status != UserStatus.DELETED;
    }

    @Override
    public boolean isAccountNonLocked() {
        return status != UserStatus.LOCKED && status != UserStatus.SUSPENDED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // integrate with password policy later if needed
    }

    @Override
    public boolean isEnabled() {
        return status == UserStatus.ACTIVE;
    }
}
