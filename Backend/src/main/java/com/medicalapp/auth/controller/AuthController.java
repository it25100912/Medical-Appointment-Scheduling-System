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

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor

public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final FileUserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    private final FilePatientRepository patientRepository;
    private final FileDoctorRepository doctorRepository;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(new JwtResponse(jwt, user.getId(), user.getEmail(), user.getRole().name(), user.getName()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        User user = User.builder()
                .email(registerRequest.getEmail())
                .password(encoder.encode(registerRequest.getPassword()))
                .name(registerRequest.getName())
                .nic(registerRequest.getNic())
                .phone(registerRequest.getPhone())
                .licenseNumber(registerRequest.getLicenseNumber())
                .role(User.Role.valueOf(registerRequest.getRole()))
                .build();

        userRepository.save(user);

        // Also create domain record for patients and doctors so admin dashboard can see them
        try {
            if (user.getRole() == User.Role.PATIENT) {
                Patient p = new Patient();
                p.setId(user.getId());
                p.setName(user.getName());
                p.setEmail(user.getEmail());
                p.setPhone(user.getPhone());
                p.setNic(user.getNic());
                p.setPassword(user.getPassword());
                // populate optional patient fields if provided in request
                if (registerRequest.getDateOfBirth() != null) p.setDateOfBirth(registerRequest.getDateOfBirth());
                if (registerRequest.getBloodGroup() != null) p.setBloodGroup(registerRequest.getBloodGroup());
                if (registerRequest.getAddress() != null) p.setAddress(registerRequest.getAddress());
                patientRepository.save(p);
            } else if (user.getRole() == User.Role.DOCTOR) {
                Doctor d = new GeneralDoctor();
                d.setId(user.getId());
                d.setName(user.getName());
                d.setEmail(user.getEmail());
                d.setPhone(user.getPhone());
                d.setLicenseNumber(user.getLicenseNumber());
                d.setPassword(user.getPassword());
                doctorRepository.save(d);
            }
        } catch (Exception e) {
            // Non-fatal: if saving domain entities fails, user auth record still exists
            System.err.println("Warning: Failed to save domain record for user " + registerRequest.getEmail() + ": " + e.getMessage());
            e.printStackTrace();
        }

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
    @Data
    @AllArgsConstructor
    public static class MessageResponse {
        private String message;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Data
    @AllArgsConstructor
    public static class JwtResponse {
        private String token;
        private Long id;
        private String email;
        private String role;
        private String name;
    }

    @Data
    public static class RegisterRequest {
        private String email;
        private String password;
        private String name;
        private String nic;
        private String phone;
        private String licenseNumber;
        private String role;
        private String dateOfBirth;
        private String bloodGroup;
        private String address;
    }   

}
