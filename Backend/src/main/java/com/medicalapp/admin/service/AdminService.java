package com.medicalapp.admin.service;

import com.medicalapp.admin.dto.AdminRequestDTO;
import com.medicalapp.admin.dto.AdminResponseDTO;
import com.medicalapp.admin.entity.Admin;
import com.medicalapp.admin.repository.FileAdminRepository;
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

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService implements IAdminService {

    private final FileAdminRepository adminRepository;
    private final FileUserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public AdminResponseDTO createAdmin(AdminRequestDTO dto) {
        Admin admin = new Admin();
        admin.setUsername(dto.getUsername());
        admin.setEmail(dto.getEmail());
        admin.setPassword(passwordEncoder.encode(dto.getPassword()));
        admin.setRole(dto.getRole());

        Admin saved = adminRepository.save(admin);

        // create corresponding User record in users.txt
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

    @Override
    public List<AdminResponseDTO> getAllAdmins() {
        List<AdminResponseDTO> list = adminRepository.findAll().stream()
                .map(this::mapEntityToResponseDto)
                .collect(Collectors.toList());

        // Include users from users.txt who are admins but may not exist in admins.txt
        userRepository.findAll().stream()
                .filter(u -> u.getRole() == com.medicalapp.auth.entity.User.Role.ADMIN)
                .forEach(u -> {
                    boolean exists = adminRepository.findById(u.getId()).isPresent() ||
                            adminRepository.findAll().stream().anyMatch(a -> a.getEmail().equalsIgnoreCase(u.getEmail()));
                    if (!exists) {
                        AdminResponseDTO dto = new AdminResponseDTO();
                        dto.setId(u.getId());
                        dto.setUsername(u.getName());
                        dto.setEmail(u.getEmail());
                        dto.setRole(Admin.AdminRole.ADMIN);
                        list.add(dto);
                    }
                });

        return list;
    }

    @Override
    public AdminResponseDTO getAdminById(Long id) {
        return adminRepository.findById(id)
                .map(this::mapEntityToResponseDto)
                .orElseGet(() -> userRepository.findById(id)
                        .filter(u -> u.getRole() == User.Role.ADMIN)
                        .map(u -> {
                            AdminResponseDTO dto = new AdminResponseDTO();
                            dto.setId(u.getId());
                            dto.setUsername(u.getName());
                            dto.setEmail(u.getEmail());
                            dto.setRole(Admin.AdminRole.ADMIN);
                            return dto;
                        }).orElseThrow(() -> new ResourceNotFoundException("Admin not found with ID: " + id)));
    }

    @Override
    public AdminResponseDTO updateAdmin(Long id, AdminRequestDTO dto) {
        Admin existing = adminRepository.findById(id).orElse(null);

        if (existing == null) {
            // Try to create from users.txt for externally-registered/default admins
            User user = userRepository.findById(id).orElse(null);
            if (user == null || user.getRole() != User.Role.ADMIN) {
                throw new ResourceNotFoundException("Admin not found");
            }

            Admin admin = new Admin();
            admin.setId(user.getId());
            admin.setUsername(user.getName());
            admin.setEmail(user.getEmail());
            admin.setPassword(user.getPassword());
            admin.setRole(dto.getRole());

            if (dto.getUsername() != null) admin.setUsername(dto.getUsername());
            if (dto.getEmail() != null) admin.setEmail(dto.getEmail());
            if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
                admin.setPassword(passwordEncoder.encode(dto.getPassword()));
            }

            Admin saved = adminRepository.save(admin);

            // Sync back to users.txt
            user.setName(saved.getUsername());
            user.setEmail(saved.getEmail());
            user.setPassword(saved.getPassword());
            userRepository.save(user);

            return mapEntityToResponseDto(saved);
        }

        existing.setUsername(dto.getUsername());
        existing.setEmail(dto.getEmail());
        existing.setRole(dto.getRole());

        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        Admin saved = adminRepository.save(existing);

        // Also update corresponding User record
        userRepository.findById(id).ifPresent(user -> {
            user.setEmail(saved.getEmail());
            user.setName(saved.getUsername());
            user.setRole(User.Role.ADMIN);
            if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
                user.setPassword(saved.getPassword());
            }
            userRepository.save(user);
        });

        return mapEntityToResponseDto(saved);
    }

    @Override
    public void deleteAdmin(Long id) {
        Long currentAdminId = com.medicalapp.common.util.SecurityUtils.getCurrentUserId();
        if (id.equals(currentAdminId)) {
            throw new IllegalStateException("You cannot delete your own admin account.");
        }
        adminRepository.deleteById(id);
        userRepository.deleteById(id);
    }

    @Override
    public void changePassword(Long id, String oldPassword, String newPassword) {
        Admin admin = adminRepository.findById(id).orElse(null);

        if (admin == null) {
            // Fallback for default super admin
            User user = userRepository.findById(id)
                    .filter(u -> u.getRole() == User.Role.ADMIN)
                    .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                throw new UnauthorizedException("Invalid old password");
            }

            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return;
        }

        if (!passwordEncoder.matches(oldPassword, admin.getPassword())) {
            throw new UnauthorizedException("Invalid old password");
        }

        admin.setPassword(passwordEncoder.encode(newPassword));
        Admin saved = adminRepository.save(admin);

        // sync password to users.txt
        userRepository.findById(saved.getId()).ifPresent(user -> {
            user.setPassword(saved.getPassword());
            userRepository.save(user);
        });
    }

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
