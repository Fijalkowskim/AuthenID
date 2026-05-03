package com.fijalkowskim.authenid.controller.admin;

import com.fijalkowskim.authenid.dto.user.UserCreateRequest;
import com.fijalkowskim.authenid.dto.user.UserResponse;
import com.fijalkowskim.authenid.dto.user.UserUpdateRequest;
import com.fijalkowskim.authenid.model.role.Role;
import com.fijalkowskim.authenid.model.user.User;
import com.fijalkowskim.authenid.repository.role.RoleRepository;
import com.fijalkowskim.authenid.service.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Administrative REST API for managing users.
 * All endpoints require the SYSTEM_ADMIN role.
 */
@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('SYSTEM_ADMIN')")
public class UserAdminController {

    private final UserService userService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs the controller with required dependencies.
     *
     * @param userService     service for user operations
     * @param roleRepository  repository for resolving roles by name
     * @param passwordEncoder encoder used to hash new user passwords
     */
    public UserAdminController(UserService userService,
                                RoleRepository roleRepository,
                                PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Returns a list of all registered users.
     *
     * @return list of user response DTOs
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.findAll().stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    /**
     * Returns a single user by internal ID.
     *
     * @param id user identifier
     * @return user response DTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        return userService.findById(id)
                .map(UserResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Creates a new user. Password is provided in plain text and encoded before persisting.
     *
     * @param request user creation parameters
     * @return created user response with HTTP 201
     */
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        Set<Role> roles = resolveRoles(request.roleNames());

        User user = User.builder()
                .username(request.username())
                .passwordHash(passwordEncoder.encode(request.password()))
                .email(request.email())
                .phoneNumber(request.phoneNumber())
                .roles(roles)
                .build();

        User saved = userService.create(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(saved));
    }

    /**
     * Updates an existing user (full replacement of mutable fields).
     *
     * @param id      user identifier
     * @param request update parameters
     * @return updated user response
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
                                                    @Valid @RequestBody UserUpdateRequest request) {
        Set<Role> roles = resolveRoles(request.roleNames());

        User patch = User.builder()
                .username(request.username())
                .email(request.email())
                .phoneNumber(request.phoneNumber())
                .emailVerified(request.emailVerified())
                .status(request.status())
                .roles(roles)
                .build();

        User updated = userService.update(id, patch);
        return ResponseEntity.ok(UserResponse.from(updated));
    }

    /**
     * Deletes a user by ID.
     *
     * @param id user identifier
     * @return HTTP 204 on success
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Set<Role> resolveRoles(Set<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            return new LinkedHashSet<>();
        }
        return roleNames.stream()
                .map(name -> roleRepository.findByName(name)
                        .orElseThrow(() -> new EntityNotFoundException("Role not found: " + name)))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
