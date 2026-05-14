package com.medicalapp.doctor.entity;

import com.medicalapp.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "doctors")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "sub_doctor_type")
@EqualsAndHashCode(callSuper = true)
public abstract class Doctor extends BaseEntity {

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Invalid email format")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phone;

    private String specialization;

    @Enumerated(EnumType.STRING)
    private DoctorType doctorType;

    private Double consultationFee;

    private String licenseNumber;
    private Integer experience;

    private String availableDays;
    private LocalTime availableFrom;
    private LocalTime availableTo;

    public enum DoctorType {
        GENERAL, SPECIALIST
    }

    public abstract double calculateConsultationFee();
   
    

}
