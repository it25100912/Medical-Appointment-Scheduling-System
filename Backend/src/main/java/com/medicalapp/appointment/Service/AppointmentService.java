package com.medicalapp.appointment.service;

import com.medicalapp.appointment.dto.AppointmentDTO;
import com.medicalapp.appointment.entity.Appointment;
import com.medicalapp.appointment.repository.FileAppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class AppointmentService implements IAppointmentService {
    private final FileAppointmentRepository appointmentRepository;

    @Override
    public AppointmentDTO bookAppointment(AppointmentDTO dto) {
        Appointment app = mapDtoToEntity(dto);
        app.setStatus(Appointment.AppointmentStatus.CONFIRMED);
        return mapEntityToDto(appointmentRepository.save(app));
    }

    @Override
    public List<AppointmentDTO> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(this::mapEntityToDto)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public AppointmentDTO getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .map(this::mapEntityToDto)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
    }

    @Override
    public List<AppointmentDTO> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findAll().stream()
                .filter(a -> a.getPatientId().equals(patientId))
                .map(this::mapEntityToDto)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<AppointmentDTO> getAppointmentsByDoctor(Long doctorId) {
        return appointmentRepository.findAll().stream()
                .filter(a -> a.getDoctorId().equals(doctorId))
                .map(this::mapEntityToDto)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public AppointmentDTO rescheduleAppointment(Long id, java.time.LocalDate newDate, java.time.LocalTime newTime) {
        Appointment app = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        app.setAppointmentDate(newDate);
        app.setAppointmentTime(newTime);
        return mapEntityToDto(appointmentRepository.save(app));
    }

    @Override
    public AppointmentDTO cancelAppointment(Long id) {
        Appointment app = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        app.setStatus(Appointment.AppointmentStatus.CANCELLED);
        return mapEntityToDto(appointmentRepository.save(app));
    }

    @Override
    public AppointmentDTO completeAppointment(Long id) {
        Appointment app = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        app.setStatus(Appointment.AppointmentStatus.COMPLETED);
        return mapEntityToDto(appointmentRepository.save(app));
    }

    @Override
    public AppointmentDTO updateAppointment(Long id, AppointmentDTO dto) {
        Appointment app = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        if (dto.getPatientId() != null) app.setPatientId(dto.getPatientId());
        if (dto.getDoctorId() != null) app.setDoctorId(dto.getDoctorId());
        if (dto.getAppointmentDate() != null) app.setAppointmentDate(dto.getAppointmentDate());
        if (dto.getAppointmentTime() != null) app.setAppointmentTime(dto.getAppointmentTime());
        if (dto.getReason() != null) app.setReason(dto.getReason());
        if (dto.getStatus() != null) app.setStatus(dto.getStatus());

        return mapEntityToDto(appointmentRepository.save(app));
    }

    @Override
    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }

    private Appointment mapDtoToEntity(com.medicalapp.appointment.dto.AppointmentDTO dto) {
        Appointment app = new Appointment();
        app.setId(dto.getId());
        app.setPatientId(dto.getPatientId());
        app.setDoctorId(dto.getDoctorId());
        app.setAppointmentDate(dto.getAppointmentDate());
        app.setAppointmentTime(dto.getAppointmentTime());
        app.setReason(dto.getReason());
        app.setStatus(dto.getStatus());
        return app;
    }

    private com.medicalapp.appointment.dto.AppointmentDTO mapEntityToDto(Appointment app) {
        com.medicalapp.appointment.dto.AppointmentDTO dto = new com.medicalapp.appointment.dto.AppointmentDTO();
        dto.setId(app.getId());
        dto.setPatientId(app.getPatientId());
        dto.setDoctorId(app.getDoctorId());
        dto.setAppointmentDate(app.getAppointmentDate());
        dto.setAppointmentTime(app.getAppointmentTime());
        dto.setReason(app.getReason());
        dto.setStatus(app.getStatus());
        return dto;
    }
}
