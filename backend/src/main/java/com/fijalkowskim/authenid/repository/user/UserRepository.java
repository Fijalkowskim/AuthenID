package com.fijalkowskim.authenid.repository.user;

import com.fijalkowskim.authenid.model.user.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    Optional<User> findWithRolesByUsername(String username);

    @Query("SELECT u FROM User u")
    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    List<User> findAllWithRoles();
}
