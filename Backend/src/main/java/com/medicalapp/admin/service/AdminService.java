package com.medicalapp.admin.service;

import com.medicalapp.admin.dto.AdminRequestDTO;
import com.medicalapp.admin.dto.AdminResponseDTO;
import com.medicalapp.admin.entity.Admin;
import com.medicalapp.admin.repository.AdminRepository;
import com.medicalapp.auth.entity.User;
import com.medicalapp.auth.repository.FileUserRepository;
import com.medicalapp.common.exception.ResourceNotFoundException;
import com.medicalapp.common.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


 // Service class for handling Admin related business logic

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService implements IAdminService {

    // Repository for admin database operations
    private final AdminRepository adminRepository;

    // Repository for user file operations
    private final FileUserRepository userRepository;

    // Password encoder for encrypting passwords
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


     // Create a new admin

    @Override
    public AdminResponseDTO createAdmin(AdminRequestDTO dto) {

        // Create new admin entity
        Admin admin = new Admin();

        admin.setUsername(dto.getUsername());
        admin.setEmail(dto.getEmail());

        // Encrypt password before saving
        admin.setPassword(passwordEncoder.encode(dto.getPassword()));

        admin.setRole(dto.getRole());

        // Save admin to database
        Admin saved = adminRepository.save(admin);

        // Create corresponding user record in users.txt
        if (saved.getId() != null) {

            User user = new User();

            user.setId(saved.getId());
            user.setEmail(saved.getEmail());
            user.setPassword(saved.getPassword());
            user.setName(saved.getUsername());
            user.setRole(User.Role.ADMIN);

            userRepository.save(user);
        }

        return mapEntityToResponseDto(saved);
    }


     // Retrieve all admins

    @Override
    public List<AdminResponseDTO> getAllAdmins() {

        return adminRepository.findAll().stream()
                .map(this::mapEntityToResponseDto)
                .collect(Collectors.toList());
    }


     // Retrieve admin by ID

    @Override
    public AdminResponseDTO getAdminById(Long id) {

        Admin admin = adminRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Admin not found"));

        return mapEntityToResponseDto(admin);
    }


     // Update existing admin details

    @Override
    public AdminResponseDTO updateAdmin(Long id, AdminRequestDTO dto) {

        // Find existing admin
        Admin existing = adminRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Admin not found"));

        // Update admin details
        existing.setUsername(dto.getUsername());
        existing.setEmail(dto.getEmail());
        existing.setRole(dto.getRole());

        // Save updated admin
        Admin saved = adminRepository.save(existing);

        // Update corresponding User record if exists
        userRepository.findById(saved.getId()).ifPresent(user -> {

            user.setEmail(saved.getEmail());
            user.setName(saved.getUsername());
            user.setRole(User.Role.ADMIN);

            userRepository.save(user);
        });

        return mapEntityToResponseDto(saved);
    }


     // Delete admin by ID

    @Override
    public void deleteAdmin(Long id) {

        // Delete admin record
        adminRepository.deleteById(id);

        // Delete corresponding user record if exists
        userRepository.deleteById(id);
    }


     // Change admin password

    @Override
    public void changePassword(Long id,
                               String oldPassword,
                               String newPassword) {

        // Find admin by ID
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Admin not found"));

        // Validate old password
        if (!passwordEncoder.matches(oldPassword, admin.getPassword())) {
            throw new UnauthorizedException("Invalid old password");
        }

        // Encode and update password
        admin.setPassword(passwordEncoder.encode(newPassword));

        Admin saved = adminRepository.save(admin);

        // Sync updated password to users.txt
        userRepository.findById(saved.getId()).ifPresent(user -> {

            user.setPassword(saved.getPassword());

            userRepository.save(user);
        });
    }


     // Convert Admin entity into AdminResponseDTO

    private AdminResponseDTO mapEntityToResponseDto(Admin admin) {

        AdminResponseDTO dto = new AdminResponseDTO();

        dto.setId(admin.getId());
        dto.setUsername(admin.getUsername());
        dto.setEmail(admin.getEmail());
        dto.setRole(admin.getRole());
        dto.setCreatedAt(admin.getCreatedAt());
        dto.setUpdatedAt(admin.getUpdatedAt());

        return dto;
    }
}