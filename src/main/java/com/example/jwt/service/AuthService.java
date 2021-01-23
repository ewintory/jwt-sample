package com.example.jwt.service;

import com.example.jwt.exception.AuthException;
import com.example.jwt.repo.UserEntity;
import com.example.jwt.repo.UserRepository;
import com.example.jwt.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public final class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
        UserRepository userRepository,
        AuthenticationManager authenticationManager,
        JwtTokenProvider jwtTokenProvider,
        PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    public String login(String username, String password) {
        // Validate user credentials
        UserEntity user = userRepository.findByUsername(username)
            .orElseThrow(() -> {
                log.error("Failed to validate user {}", username);
                return new AuthException("Invalid credentials");
            });

        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.error("Password doesn't match for user {}", username);
            throw new AuthException("Invalid credentials");
        }

        log.debug("Credentials for user {} successfully validated", username);

        // Generate access token
        var token = jwtTokenProvider.createToken(user.getUsername(), user.getId());
        log.debug("Access token for user {} successfully generated", username);

        return token;
    }

}
