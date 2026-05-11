package com.medicalapp.admin.service;

import com.medicalapp.admin.dto.AdminRequestDTO;
import com.medicalapp.admin.dto.AdminResponseDTO;
import com.medicalapp.admin.entity.Admin;
import com.medicalapp.admin.repository.FileAdminRepository;
import com.medicalapp.common.exception.DuplicateEntryException;
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
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public AdminResponseDTO createAdmin(AdminRequestDTO dto) {
        Admin admin = new Admin();
        admin.setUsername(dto.getUsername());
        admin.setEmail(dto.getEmail());
        admin.setPassword(passwordEncoder.encode(dto.getPassword()));
        admin.setRole(dto.getRole());

        return mapEntityToResponseDto(adminRepository.save(admin));
    }

    @Override
    public List<AdminResponseDTO> getAllAdmins() {
        return adminRepository.findAll().stream()
                .map(this::mapEntityToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public AdminResponseDTO getAdminById(Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
        return mapEntityToResponseDto(admin);
    }

    @Override
    public AdminResponseDTO updateAdmin(Long id, AdminRequestDTO dto) {
        Admin existing = adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
        
        existing.setUsername(dto.getUsername());
        existing.setEmail(dto.getEmail());
        existing.setRole(dto.getRole());

        return mapEntityToResponseDto(adminRepository.save(existing));
    }

    @Override
    public void deleteAdmin(Long id) {
        adminRepository.deleteById(id);
    }

    @Override
    public void changePassword(Long id, String oldPassword, String newPassword) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        if (!passwordEncoder.matches(oldPassword, admin.getPassword())) {
            throw new UnauthorizedException("Invalid old password");
        }

        admin.setPassword(passwordEncoder.encode(newPassword));
        adminRepository.save(admin);
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
