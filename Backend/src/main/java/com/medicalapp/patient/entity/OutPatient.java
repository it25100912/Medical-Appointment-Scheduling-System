package com.medicalapp.patient.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@DiscriminatorValue("OUTPATIENT")
@EqualsAndHashCode(callSuper = true)
public class OutPatient extends Patient {
}
