package com.medicalapp.doctor.model;

import com.medicalapp.common.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Doctor extends User {
    private String specialization;
    private String licenseNumber;
    private String availability; // e.g. MON-FRI 8-5

    @Override
    public String getDisplayInfo() {
        return "Doctor: Dr. " + getFullName() + " [" + specialization + "]";
    }
}
