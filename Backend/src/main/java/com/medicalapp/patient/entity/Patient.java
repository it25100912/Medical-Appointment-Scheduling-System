package com.medicalapp.patient.entity;

import com.medicalapp.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "patients")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "sub_patient_type")
@EqualsAndHashCode(callSuper = true)
public class Patient extends BaseEntity {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "NIC is required")
    @Column(unique = true)
    private String nic;

    @NotBlank(message = "Phone number is required")
    private String phone;

    @Email(message = "Invalid email format")
    private String email;

    private String address;
    private String bloodGroup;
    private String dateOfBirth;

    @Column(columnDefinition = "TEXT")
    private String medicalHistory;

    @Enumerated(EnumType.STRING)
    private PatientType patientType;

    public enum PatientType {
        OUTPATIENT, INPATIENT
    }
}
