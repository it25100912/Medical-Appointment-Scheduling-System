package com.medicalapp.admin.entity;

import com.medicalapp.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "admins")
@EqualsAndHashCode(callSuper = true)
public class Admin extends BaseEntity {

    @NotBlank(message = "Username is required")
    @Column(unique = true)
    private String username;

    @Email(message = "Invalid email format")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @Enumerated(EnumType.STRING)
    private AdminRole role;

    public enum AdminRole {
        SUPER_ADMIN, ADMIN
    }
}
