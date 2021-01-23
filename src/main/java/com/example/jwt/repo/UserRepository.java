package com.example.jwt.repo;

import java.util.Optional;

public interface UserRepository {

    Optional<UserEntity> findByUsername(String username);

}
