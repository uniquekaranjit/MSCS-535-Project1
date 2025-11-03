package com.unique.securewebapp.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AuthDtos {
    public static class RegisterRequest {
        @NotBlank
        @Pattern(regexp = "^[A-Za-z0-9._@-]{3,64}$")
        public String username;

        @NotBlank
        @Size(min = 8, max = 100)
        public String password;
    }

    public static class LoginRequest {
        @NotBlank
        public String username;
        @NotBlank
        public String password;
    }
}
