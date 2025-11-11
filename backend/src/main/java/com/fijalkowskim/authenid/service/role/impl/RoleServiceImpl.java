package com.fijalkowskim.authenid.service.role.impl;

import com.fijalkowskim.authenid.model.role.Role;
import com.fijalkowskim.authenid.repository.role.RoleRepository;
import com.fijalkowskim.authenid.service.role.RoleService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Role create(Role role) {
        if (roleRepository.existsByName(role.getName())) {
            throw new IllegalArgumentException("Role already exists");
        }
        return roleRepository.save(role);
    }

    @Override
    public Role update(Long id, Role updated) {
        Role existing = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setPermissions(updated.getPermissions());
        return roleRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        roleRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> findById(Long id) {
        return roleRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> findByName(String name) {
        return roleRepository.findByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> findAll() {
        return roleRepository.findAll();
    }
}
