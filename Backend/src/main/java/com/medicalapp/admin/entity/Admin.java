package com.medicalapp.admin.entity;

import com.medicalapp.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Entity class representing an Admin
 */
@Data
@Entity
@Table(name = "admins")
@EqualsAndHashCode(callSuper = true)
public class Admin extends BaseEntity {


     // Admin username

    @NotBlank(message = "Username is required")
    @Column(unique = true)
    private String username;


     // Admin email address

    @Email(message = "Invalid email format")
    @Column(unique = true)
    private String email;


     //Admin account password

    @NotBlank(message = "Password is required")
    private String password;


     // Admin role

    @Enumerated(EnumType.STRING)
    private AdminRole role;


     // Enum representing admin roles

    public enum AdminRole {
        SUPER_ADMIN,
        ADMIN
    }
}