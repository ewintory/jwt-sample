package com.example.jwt.repo;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Simple in-memory repository implementation
 */
@Component
public final class InMemUserRepository implements UserRepository {

    private final List<UserEntity> users = new ArrayList<>();

    private final PasswordEncoder passwordEncoder;

    public InMemUserRepository(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        users.add(new UserEntity(42L, "richard", passwordEncoder.encode("hello")));
        users.add(new UserEntity(1L, "1", passwordEncoder.encode("1")));
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        return users.stream()
            .filter(e -> e.getUsername().equals(username))
            .findFirst();
    }

}
