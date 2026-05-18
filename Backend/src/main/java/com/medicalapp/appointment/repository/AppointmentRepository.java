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

    List<Appointment> findByPatient_Id(Long patientId);

    List<Appointment> findByDoctor_Id(Long doctorId);

    List<Appointment> findByStatus(Appointment.AppointmentStatus status);

    List<Appointment> findByAppointmentDate(LocalDate date);

    Optional<Appointment> findByDoctor_IdAndAppointmentDateAndAppointmentTime(Long doctorId, LocalDate date, LocalTime time);
}
