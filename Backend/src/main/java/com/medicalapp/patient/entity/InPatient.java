package com.medicalapp.patient.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@DiscriminatorValue("INPATIENT")
@EqualsAndHashCode(callSuper = true)
public class InPatient extends Patient {
}
