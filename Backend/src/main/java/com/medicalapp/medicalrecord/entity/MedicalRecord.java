package com.medicalapp.medicalrecord.entity;

import com.medicalapp.common.entity.BaseEntity;
import com.medicalapp.patient.entity.Patient;
import com.medicalapp.doctor.entity.Doctor;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "medical_records")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "record_sub_type")
@EqualsAndHashCode(callSuper = true)
public abstract class MedicalRecord extends BaseEntity { // OOP: Abstraction & Inheritance

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @NotNull(message = "Record date is required")
    private LocalDate recordDate; // OOP: Encapsulation

    private String diagnosis;
    @Transient
    private Long patientId;
    @Transient
    private Long doctorId;
    private String prescription;
    private String notes;

    @Enumerated(EnumType.STRING)
    private RecordType recordType;

    public Long getPatientId() {
        if (this.patientId != null && this.patientId != 0L) {
            return this.patientId;
        }
        return this.patient != null ? this.patient.getId() : null;
    }

    public Long getDoctorId() {
        if (this.doctorId != null && this.doctorId != 0L) {
            return this.doctorId;
        }
        return this.doctor != null ? this.doctor.getId() : null;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
        if (patient != null) {
            this.patientId = patient.getId();
        }
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
        if (doctor != null) {
            this.doctorId = doctor.getId();
        }
    }

    public enum RecordType {
        PRESCRIPTION, LAB_RESULT
    }

    public abstract String getRecordSummary(); // OOP: Abstraction
}
