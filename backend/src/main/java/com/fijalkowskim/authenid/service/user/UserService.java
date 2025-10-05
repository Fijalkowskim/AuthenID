package com.fijalkowskim.authenid.service.user;

import com.fijalkowskim.authenid.model.user.User;
import java.util.List;
import java.util.Optional;

public interface UserService {

    User create(User user);

    User update(Long id, User user);

    void delete(Long id);

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findAll();

    User save(User user);
}
