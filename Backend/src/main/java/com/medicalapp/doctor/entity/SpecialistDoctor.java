package com.medicalapp.doctor.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@DiscriminatorValue("SPECIALIST")
@EqualsAndHashCode(callSuper = true)
public class SpecialistDoctor extends Doctor {

    private String specialistArea;

    @Override
    public double calculateConsultationFee() {
        return this.getConsultationFee() * 1.5;
    }
}
