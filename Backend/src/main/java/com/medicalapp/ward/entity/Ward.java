package com.medicalapp.ward.entity;

import com.medicalapp.common.entity.BaseEntity;
import com.medicalapp.doctor.entity.Doctor;
import com.medicalapp.nurse.entity.Nurse;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "wards")
@EqualsAndHashCode(callSuper = true)
public class Ward extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    private String type; // e.g., General, ICU, Maternity

    private Integer totalBeds;
    private Integer availableBeds;

    @ManyToMany
    @JoinTable(
        name = "ward_doctors",
        joinColumns = @JoinColumn(name = "ward_id"),
        inverseJoinColumns = @JoinColumn(name = "doctor_id")
    )
    private List<Doctor> doctors = new ArrayList<>();

    @OneToMany(mappedBy = "ward", cascade = CascadeType.ALL)
    private List<Nurse> nurses = new ArrayList<>();
}
