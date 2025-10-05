package com.fijalkowskim.authenid.model.role;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Role groups a set of permissions and is assigned to users.
 * Example: "ADMIN", "USER".
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "role", indexes = {
        @Index(name = "uk_role_name", columnList = "name", unique = true)
})
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 60)
    private String name;

    @Column(length = 255)
    private String description;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "permission_id", nullable = false),
            uniqueConstraints = @UniqueConstraint(name = "uk_role_permission", columnNames = {"role_id", "permission_id"}))
    private Set<Permission> permissions = new LinkedHashSet<>();
}
