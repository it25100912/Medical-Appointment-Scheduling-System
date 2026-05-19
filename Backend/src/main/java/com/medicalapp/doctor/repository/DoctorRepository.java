package com.medicalapp.doctor.repository;

import com.medicalapp.doctor.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    List<Doctor> findBySpecialization(String specialization);

    List<Doctor> findByDoctorType(Doctor.DoctorType type);

    Optional<Doctor> findByEmail(String email);

    List<Doctor> findByAvailableDaysContaining(String day);
}
