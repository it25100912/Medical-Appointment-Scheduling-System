package com.medicalapp.auth.controller;

import com.medicalapp.auth.config.JwtUtils;
import com.medicalapp.auth.entity.User;
import com.medicalapp.auth.repository.FileUserRepository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final FileUserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;


}
