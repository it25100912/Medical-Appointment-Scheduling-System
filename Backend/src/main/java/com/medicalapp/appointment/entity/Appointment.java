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
    
    public Long getPatientId() {
        if (this.patientId != null && this.patientId != 0) {
            return this.patientId;
        }
        return this.patient != null ? this.patient.getId() : null;
    }

    public Long getDoctorId() {
        if (this.doctorId != null && this.doctorId != 0) {
            return this.doctorId;
        }
        return this.doctor != null ? this.doctor.getId() : null;
    }
    
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
