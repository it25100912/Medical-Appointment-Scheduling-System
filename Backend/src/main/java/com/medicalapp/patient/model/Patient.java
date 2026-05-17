package com.medicalapp.patient.model;

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
public class Patient extends User {
    private String nic;
    private String dob;
    private String bloodGroup;
    private String address;

    @Override
    public String getDisplayInfo() {
        return "Patient: " + getFullName() + " (ID: " + getId() + ")";
    }
}
