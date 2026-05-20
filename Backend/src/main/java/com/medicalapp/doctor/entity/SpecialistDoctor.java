package com.medicalapp.doctor.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@DiscriminatorValue("SPECIALIST")
@EqualsAndHashCode(callSuper = true)
public class SpecialistDoctor extends Doctor { // OOP: Inheritance

    private String specialistArea; // OOP: Encapsulation

    @Override
    public double calculateConsultationFee() { // OOP: Polymorphism
        return this.getConsultationFee() * 1.5;
    }
}
