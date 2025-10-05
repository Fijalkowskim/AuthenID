package com.fijalkowskim.authenid.service.role.impl;

import com.fijalkowskim.authenid.model.role.Permission;
import com.fijalkowskim.authenid.repository.role.PermissionRepository;
import com.fijalkowskim.authenid.service.role.PermissionService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    @Override
    public Permission create(Permission permission) {
        if (permissionRepository.existsByName(permission.getName())) {
            throw new IllegalArgumentException("Permission already exists");
        }
        return permissionRepository.save(permission);
    }

    @Override
    public Permission update(Long id, Permission updated) {
        Permission existing = permissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Permission not found"));
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        return permissionRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        permissionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Permission> findById(Long id) {
        return permissionRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Permission> findByName(String name) {
        return permissionRepository.findByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Permission> findAll() {
        return permissionRepository.findAll();
    }
}
