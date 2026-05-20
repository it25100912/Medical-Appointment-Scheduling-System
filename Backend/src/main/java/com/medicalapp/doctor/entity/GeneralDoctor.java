package com.medicalapp.doctor.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@DiscriminatorValue("GENERAL")
@EqualsAndHashCode(callSuper = true)
public class GeneralDoctor extends Doctor { // OOP: Inheritance

    @Override
    public double calculateConsultationFee() { // OOP: Polymorphism
        return this.getConsultationFee();
    }
}
