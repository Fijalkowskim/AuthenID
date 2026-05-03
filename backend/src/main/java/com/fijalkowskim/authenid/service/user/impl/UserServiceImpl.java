package com.fijalkowskim.authenid.service.user.impl;

import com.fijalkowskim.authenid.model.user.User;
import com.fijalkowskim.authenid.repository.user.UserRepository;
import com.fijalkowskim.authenid.service.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of {@link UserService}.
 * All write operations run within a transaction; reads use read-only transactions.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User create(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already in use");
        }
        return userRepository.save(user);
    }

    @Override
    public User update(Long id, User updated) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Validate email uniqueness when it changes
        if (!existing.getEmail().equals(updated.getEmail())
                && userRepository.existsByEmail(updated.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        // Validate username uniqueness when it changes
        if (!existing.getUsername().equals(updated.getUsername())
                && userRepository.existsByUsername(updated.getUsername())) {
            throw new IllegalArgumentException("Username already in use");
        }

        existing.setEmail(updated.getEmail());
        existing.setUsername(updated.getUsername());
        existing.setPhoneNumber(updated.getPhoneNumber());
        existing.setStatus(updated.getStatus());
        existing.setEmailVerified(updated.isEmailVerified());
        if (updated.getRoles() != null) {
            existing.setRoles(updated.getRoles());
        }
        return userRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAllWithRoles();
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }
}
