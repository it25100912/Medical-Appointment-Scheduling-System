package com.medicalapp.admin.dto;

import com.medicalapp.admin.entity.Admin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


 //DTO class used for Admin request data

@Data
public class AdminRequestDTO {


     //Admin username

    @NotBlank(message = "Username is required")
    private String username;


     // Admin email address

    @Email(message = "Invalid email format")
    private String email;


     //Admin password

    @NotBlank(message = "Password is required")
    private String password;


     // Admin role (e.g., SUPER_ADMIN, ADMIN)

    private Admin.AdminRole role;
}