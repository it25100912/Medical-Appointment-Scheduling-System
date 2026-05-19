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

    // Many appointments can belong to one patient
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    // Many appointments can belong to one doctor
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    
    @NotNull(message = "Appointment date is required")
    private LocalDate appointmentDate;


    @NotNull(message = "Appointment time is required")
    private LocalTime appointmentTime;

    // Temporary field used for transferring patient ID without loading full Patient object
    @Transient
    private Long patientId;

    // Temporary field used for transferring doctor ID without loading full Doctor object
    @Transient
    private Long doctorId;

    // Returns patient ID either from transient field or from patient object
    public Long getPatientId() {
        if (this.patientId != null && this.patientId != 0) {
            return this.patientId;
        }
        return this.patient != null ? this.patient.getId() : null;
    }

    // Returns doctor ID either from transient field or from doctor object
    public Long getDoctorId() {
        if (this.doctorId != null && this.doctorId != 0) {
            return this.doctorId;
        }
        return this.doctor != null ? this.doctor.getId() : null;
    }
    
    private String reason;

    // Status of the appointment
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    // Type of appointment
    @Enumerated(EnumType.STRING)
    private AppointmentType type;


    private String notes;

    // Enum for appointment status
    public enum AppointmentStatus {
        PENDING, CONFIRMED, CANCELLED, COMPLETED
    }

    // Enum for appointment type
    public enum AppointmentType {
        ONLINE, IN_PERSON
    }
}
