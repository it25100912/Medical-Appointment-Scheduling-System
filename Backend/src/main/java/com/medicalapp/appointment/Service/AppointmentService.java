package com.medicalapp.appointment.service;

import com.medicalapp.appointment.dto.AppointmentDTO;
import com.medicalapp.appointment.entity.Appointment;
import com.medicalapp.appointment.repository.FileAppointmentRepository;
import com.medicalapp.billingandpayment.repository.FileBillingRepository;
import com.medicalapp.billingandpayment.entity.Billing;
import com.medicalapp.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentService implements IAppointmentService {
    private final FileAppointmentRepository appointmentRepository;
    private final com.medicalapp.patient.repository.FilePatientRepository patientRepository;
    private final com.medicalapp.doctor.repository.FileDoctorRepository doctorRepository;
    private final FileBillingRepository billingRepository;

    @Override
    public AppointmentDTO bookAppointment(AppointmentDTO dto) {
        Appointment app = mapDtoToEntity(dto);
        app.setStatus(Appointment.AppointmentStatus.CONFIRMED);
        Appointment savedApp = appointmentRepository.save(app);

        // Auto-generate clinical billing receipt record
        try {
            Billing billing = new Billing();
            com.medicalapp.doctor.entity.Doctor doctor = app.getDoctor();
            double fee = (doctor != null && doctor.getConsultationFee() != null) ? doctor.getConsultationFee() : 1500.0;
            billing.setAmount(fee);
            billing.setPatientId(app.getPatientId());
            billing.setStatus(Billing.PaymentStatus.PAID);
            billing.setInvoiceNumber("INV-" + System.currentTimeMillis());
            billing.setBillingCategory("OPD Consultation");
            billing.setAppointment(savedApp);
            billingRepository.save(billing);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mapEntityToDto(savedApp);
    }

    @Override
    public List<AppointmentDTO> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public AppointmentDTO getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .map(this::mapEntityToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
    }

    @Override
    public List<AppointmentDTO> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId).stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDTO> getAppointmentsByDoctor(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId).stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public AppointmentDTO rescheduleAppointment(Long id, java.time.LocalDate newDate, java.time.LocalTime newTime) {
        
        // Find appointment first
        Appointment app = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        app.setAppointmentDate(newDate);
        app.setAppointmentTime(newTime);
        return mapEntityToDto(appointmentRepository.save(app));
    }

    @Override
    public AppointmentDTO cancelAppointment(Long id) {
        Appointment app = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        // Mark appointment as cancelled
        app.setStatus(Appointment.AppointmentStatus.CANCELLED);
        return mapEntityToDto(appointmentRepository.save(app));
    }

    @Override
    public AppointmentDTO completeAppointment(Long id) {
        Appointment app = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

         // Mark appointment as completed
        app.setStatus(Appointment.AppointmentStatus.COMPLETED);
        return mapEntityToDto(appointmentRepository.save(app));
    }

    @Override
    public AppointmentDTO updateAppointment(Long id, AppointmentDTO dto) {
        Appointment app = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        
         // Update patient details if provided
        if (dto.getPatientId() != null) {
            app.setPatientId(dto.getPatientId());
            app.setPatient(patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found")));
        }

         // Update doctor details if provided
        if (dto.getDoctorId() != null) {
            app.setDoctorId(dto.getDoctorId());
            app.setDoctor(doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found")));
        }

        // Update remaining fields if provided
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

    // Convert DTO into Entity
    private Appointment mapDtoToEntity(AppointmentDTO dto) {
        Appointment app = new Appointment();
        if (dto.getPatientId() != null) {
            app.setPatientId(dto.getPatientId());
            app.setPatient(patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found")));
        }
        if (dto.getDoctorId() != null) {
            app.setDoctorId(dto.getDoctorId());
            app.setDoctor(doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found")));
        }
        app.setAppointmentDate(dto.getAppointmentDate());
        app.setAppointmentTime(dto.getAppointmentTime());
        app.setReason(dto.getReason());
        app.setStatus(dto.getStatus());
        return app;
    }

    // Convert Entity into DTO
    private AppointmentDTO mapEntityToDto(Appointment app) {
        AppointmentDTO dto = new AppointmentDTO();
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
