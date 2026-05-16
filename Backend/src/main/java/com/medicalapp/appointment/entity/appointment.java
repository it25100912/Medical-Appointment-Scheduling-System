package com.medicalapp.appointment.entity;

import com.medicalapp.common.entity.BaseEntity;
import com.medicalapp.patient.entity.Patient;
import com.medicalapp.doctor.entity.Doctor;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "appointments")
@EqualsAndHashCode(callSuper = true)
public class Appointment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @NotNull(message = "Appointment date is required")
    private LocalDate appointmentDate;
 
    @NotNull(message = "Appointment time is required")
    private LocalTime appointmentTime;

    @Transient
    private Long patientId;
    @Transient
    private Long doctorId;
    private String reason;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    @Enumerated(EnumType.STRING)
    private AppointmentType type;

    private String notes;

    public enum AppointmentStatus {
        PENDING, CONFIRMED, CANCELLED, COMPLETED
    }

    public enum AppointmentType {
        ONLINE, IN_PERSON
    }
}
