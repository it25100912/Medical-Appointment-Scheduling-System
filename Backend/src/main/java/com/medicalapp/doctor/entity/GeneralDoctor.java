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

    @Override
    public double calculateConsultationFee() {
        return this.getConsultationFee();
    }
}
