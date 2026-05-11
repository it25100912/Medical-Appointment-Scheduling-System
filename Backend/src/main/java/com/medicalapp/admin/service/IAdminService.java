package com.medicalapp.admin.service;

import com.medicalapp.admin.dto.AdminRequestDTO;
import com.medicalapp.admin.dto.AdminResponseDTO;
import java.util.List;

public interface IAdminService {

    AdminResponseDTO createAdmin(AdminRequestDTO dto);

    List<AdminResponseDTO> getAllAdmins();

    AdminResponseDTO getAdminById(Long id);

    AdminResponseDTO updateAdmin(Long id, AdminRequestDTO dto);

    void deleteAdmin(Long id);

    void changePassword(Long id, String oldPassword, String newPassword);
}
