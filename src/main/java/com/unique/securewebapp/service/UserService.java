package com.unique.securewebapp.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unique.securewebapp.entity.User;
import com.unique.securewebapp.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public UserService(UserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @Transactional
    public void register(String username, String rawPassword) {
        String hash = encoder.encode(rawPassword);
        User user = new User(username, hash);
        repo.save(user);
    }

    public boolean authenticate(String username, String rawPassword) {
        Optional<User> u = repo.findByUsername(username);
        return u.isPresent() && encoder.matches(rawPassword, u.get().getPasswordHash());
    }
}
