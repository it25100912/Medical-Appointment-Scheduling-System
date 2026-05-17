package com.medicalapp.patient.entity;

import com.medicalapp.ward.entity.Ward;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@DiscriminatorValue("INPATIENT")
@EqualsAndHashCode(callSuper = true)
public class InPatient extends Patient {

    @ManyToOne
    @JoinColumn(name = "ward_id")
    private Ward ward;
}
