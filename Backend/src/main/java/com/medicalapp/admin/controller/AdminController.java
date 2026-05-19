package com.medicalapp.admin.controller;

import com.medicalapp.admin.dto.AdminRequestDTO;
import com.medicalapp.admin.dto.AdminResponseDTO;
import com.medicalapp.admin.service.IAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


// Controller class for handling Admin related APIs

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {

    // Service layer object injection
    private final IAdminService adminService;

    @PostMapping
    public ResponseEntity<AdminResponseDTO> createAdmin(@Valid @RequestBody AdminRequestDTO dto) {
        return new ResponseEntity<>(adminService.createAdmin(dto), HttpStatus.CREATED);
    }


    @GetMapping
    public List<AdminResponseDTO> getAllAdmins() {
        return adminService.getAllAdmins();
    }


     // Get admin details by ID

    @GetMapping("/{id}")
    public AdminResponseDTO getAdminById(@PathVariable Long id) {
        return adminService.getAdminById(id);
    }


     // Update admin details

    @PutMapping("/{id}")
    public AdminResponseDTO updateAdmin(@PathVariable Long id, @Valid @RequestBody AdminRequestDTO dto) {
        return adminService.updateAdmin(id, dto);
    }

    // Change admin password

    @PutMapping("/{id}/change-password")
    public ResponseEntity<String> changePassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        adminService.changePassword(id, body.get("oldPassword"), body.get("newPassword"));
        return ResponseEntity.ok("Password updated successfully");
    }


     // Delete admin by ID

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
        adminService.deleteAdmin(id);
        return ResponseEntity.noContent().build();
    }
}