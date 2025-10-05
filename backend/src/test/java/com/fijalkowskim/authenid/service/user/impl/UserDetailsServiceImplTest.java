package com.fijalkowskim.authenid.service.user.impl;

import com.fijalkowskim.authenid.model.role.Role;
import com.fijalkowskim.authenid.model.user.User;
import com.fijalkowskim.authenid.repository.role.RoleRepository;
import com.fijalkowskim.authenid.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class UserDetailsServiceImplTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void shouldLoadUserWithAuthorities() {
        Role role = roleRepository.save(Role.builder().name("ADMIN").build());
        User user = userRepository.save(User.builder()
                .username("john")
                .passwordHash("encoded")
                .email("john@example.com")
                .roles(Set.of(role))
                .build());

        UserDetails details = userDetailsService.loadUserByUsername("john");

        assertThat(details.getUsername()).isEqualTo("john");
        assertThat(details.getAuthorities()).extracting("authority")
                .contains("ROLE_ADMIN");
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        assertThrows(UsernameNotFoundException.class, () ->
                userDetailsService.loadUserByUsername("missing"));
    }
}
