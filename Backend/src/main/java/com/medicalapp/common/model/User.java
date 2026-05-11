package com.medicalapp.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class User implements Serializable {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String role; // PATIENT, DOCTOR, ADMIN

    // Abstraction: Common behavior for all users
    public abstract String getDisplayInfo();
}
