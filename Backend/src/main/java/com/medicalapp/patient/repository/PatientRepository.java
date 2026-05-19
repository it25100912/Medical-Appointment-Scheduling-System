package com.medicalapp.patient.repository;

import com.medicalapp.patient.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByNic(String nic);

    Optional<Patient> findByPhone(String phone);

    Optional<Patient> findByEmail(String email);

    Optional<Patient> findByPatientType(Patient.PatientType type);
}
