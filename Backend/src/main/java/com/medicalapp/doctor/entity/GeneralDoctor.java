package com.medicalapp.doctor.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@DiscriminatorValue("GENERAL")
@EqualsAndHashCode(callSuper = true)
public class GeneralDoctor extends Doctor {

        // Overrides the abstract method from Doctor class
    // Returns the normal consultation fee for a general doctor
    @Override
    public double calculateConsultationFee() {
        return this.getConsultationFee();
    }
}