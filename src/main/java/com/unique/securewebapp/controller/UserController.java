package com.unique.securewebapp.controller;

import static org.springframework.http.ResponseEntity.*;

import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.unique.securewebapp.service.UserService;
import com.unique.securewebapp.controller.AuthDtos.LoginRequest;
import com.unique.securewebapp.controller.AuthDtos.RegisterRequest;

@RestController
@RequestMapping("/api")
@Validated
public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Validated RegisterRequest req) {
        try {
            service.register(req.username, req.password);
            return ok(Map.of("message", "User registered successfully!"));
        } catch (DataIntegrityViolationException e) {
            return status(HttpStatus.CONFLICT).body(Map.of("error", "Username already exists"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Validated LoginRequest req) {
        boolean okLogin = service.authenticate(req.username, req.password);
        if (okLogin) {
            return ok(Map.of("message", "Login successful", "username", req.username));
        }
        return status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid credentials"));
    }
}
