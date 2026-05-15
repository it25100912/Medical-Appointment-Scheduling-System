package com.medicalapp.admin.dto;

import com.medicalapp.admin.entity.Admin;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdminResponseDTO {

    private Long id;
    private String username;
    private String email;
    private Admin.AdminRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
