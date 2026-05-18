package com.medicalapp.appointment.repository;

import com.medicalapp.appointment.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

     // Find all appointments by patient ID using nested property from Patient entity
    List<Appointment> findByPatient_Id(Long patientId);


    List<Appointment> findByDoctor_Id(Long doctorId);

    // Custom default method for easier access using patientId
    default List<Appointment> findByPatientId(Long patientId) {
        return findByPatient_Id(patientId);
    }

    // Custom default method for easier access using doctorId
    default List<Appointment> findByDoctorId(Long doctorId) {
        return findByDoctor_Id(doctorId);
    }

    // Find appointments by appointment status
    List<Appointment> findByStatus(Appointment.AppointmentStatus status);

    // Find appointments by appointment date
    List<Appointment> findByAppointmentDate(LocalDate date);

     // Check if a doctor already has an appointment at the same date and time
    // Used to prevent appointment booking conflicts
    Optional<Appointment> findByDoctor_IdAndAppointmentDateAndAppointmentTime(Long doctorId, LocalDate date, LocalTime time);
}
