package com.medicalapp.medicalrecord.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@DiscriminatorValue("LAB_RESULT") //Used in inheritance to identify this subclass type
@EqualsAndHashCode(callSuper = true)
public class LabRecord extends MedicalRecord {

    private String testName;
    private String testResult;
    private String referenceRange;

    @Override
    public String getRecordSummary() {
        return String.format("Lab Result | Test: %s | Result: %s (Normal range: %s)",
                testName, testResult, referenceRange);
    }
}
