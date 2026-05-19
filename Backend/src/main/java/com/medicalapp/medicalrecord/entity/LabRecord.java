package com.medicalapp.medicalrecord.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@DiscriminatorValue("LAB_RESULT")
@EqualsAndHashCode(callSuper = true)
public class LabRecord extends MedicalRecord { // OOP: Inheritance

    private String testName; // OOP: Encapsulation
    private String testResult;
    private String referenceRange;

    @Override
    public String getRecordSummary() { // OOP: Polymorphism
        return String.format("Lab Result | Test: %s | Result: %s (Normal range: %s)",
                testName, testResult, referenceRange);
    }
}
