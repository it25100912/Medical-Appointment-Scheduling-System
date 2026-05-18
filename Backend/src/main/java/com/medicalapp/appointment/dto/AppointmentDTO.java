package com.medicalapp.appointment.dto;

import com.medicalapp.appointment.entity.Appointment;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AppointmentDTO {

    private Long id;
    
    // Patient ID is required for creating an appointment
    @NotNull(message = "Patient ID is required")
    private Long patientId;

    // Doctor ID is required for creating an appointment
    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    @NotNull(message = "Appointment date is required")
    private LocalDate appointmentDate;

    @NotNull(message = "Appointment time is required")
    private LocalTime appointmentTime;

    // Status of the appointment
    private Appointment.AppointmentStatus status;

    private Appointment.AppointmentType type;

    // Reason for booking the appointment
    private String reason;
    
    // Additional notes related to the appointment
    private String notes;
}
