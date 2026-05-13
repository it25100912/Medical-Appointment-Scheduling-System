package com.medicalapp.doctor.dto;

import com.medicalapp.doctor.entity.Doctor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalTime;

@Data
public class DoctorDTO {

    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phone;

    private String specialization;

    private Doctor.DoctorType doctorType;

    private Double consultationFee;

    private String availableDays;

    private LocalTime availableFrom;

    private LocalTime availableTo;

    private String licenseNumber;
    private Integer experience;

    private String specialistArea;
}
