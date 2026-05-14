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
public abstract class MedicalRecord extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @NotNull(message = "Record date is required")
    private LocalDate recordDate;

    private String diagnosis;
    @Transient
    private Long patientId;
    @Transient
    private Long doctorId;
    private String prescription;
    private String notes;

    @Enumerated(EnumType.STRING)
    private RecordType recordType;

    public enum RecordType {
        PRESCRIPTION, LAB_RESULT
    }

    public abstract String getRecordSummary();
}
