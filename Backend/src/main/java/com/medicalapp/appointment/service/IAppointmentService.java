package com.medicalapp.appointment.service;

import com.medicalapp.appointment.dto.AppointmentDTO;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface IAppointmentService {

    AppointmentDTO bookAppointment(AppointmentDTO dto);

    List<AppointmentDTO> getAllAppointments();

    AppointmentDTO getAppointmentById(Long id);

    List<AppointmentDTO> getAppointmentsByPatient(Long patientId);

    List<AppointmentDTO> getAppointmentsByDoctor(Long doctorId);

    AppointmentDTO rescheduleAppointment(Long id, LocalDate newDate, LocalTime newTime);

    AppointmentDTO cancelAppointment(Long id);

    AppointmentDTO completeAppointment(Long id);

    AppointmentDTO updateAppointment(Long id, AppointmentDTO dto);

    void deleteAppointment(Long id);
}
