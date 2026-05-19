package com.medicalapp.medicalrecord.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@DiscriminatorValue("PRESCRIPTION")//Identifies this subtype in single-table inheritance
@EqualsAndHashCode(callSuper = true)
public class PrescriptionRecord extends MedicalRecord {

    private String medicineName;
    private String dosage;
    private String duration;

    // Returns formatted summary of prescription record
    @Override
    public String getRecordSummary() {
        return String.format("Prescription | Diagnosis: %s | Medicine: %s %s for %s",
                getDiagnosis(), medicineName, dosage, duration);
    }
}
