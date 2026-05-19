package com.medicalapp.appointment.controller;

import com.medicalapp.appointment.dto.AppointmentDTO;
import com.medicalapp.appointment.service.IAppointmentService;
import com.medicalapp.common.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AppointmentController {

    private final IAppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<AppointmentDTO> bookAppointment(@Valid @RequestBody AppointmentDTO dto) {
        return new ResponseEntity<>(appointmentService.bookAppointment(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public List<AppointmentDTO> getAllAppointments() {
        // Patients can only view their own appointments
        if (SecurityUtils.isPatient()) {
            Long patientId = SecurityUtils.getCurrentUserId();
            return appointmentService.getAppointmentsByPatient(patientId);
        }
        // Doctors and admins can see all appointments
        return appointmentService.getAllAppointments();
    }

    @GetMapping("/{id}")
    public AppointmentDTO getAppointmentById(@PathVariable Long id) {
        AppointmentDTO appointment = appointmentService.getAppointmentById(id);
        
        // Patients can only view their own appointments
        if (SecurityUtils.isPatient()) {
            Long currentPatientId = SecurityUtils.getCurrentUserId();
            if (!appointment.getPatientId().equals(currentPatientId)) {
                throw new RuntimeException("Unauthorized: You can only access your own appointments");
            }
        }
        return appointment;
    }

    @GetMapping("/patient/{patientId}")
    public List<AppointmentDTO> getAppointmentsByPatient(@PathVariable Long patientId) {
        // Patients can only view their own appointments
        if (SecurityUtils.isPatient()) {
            Long currentPatientId = SecurityUtils.getCurrentUserId();
            if (!patientId.equals(currentPatientId)) {
                throw new RuntimeException("Unauthorized: You can only access your own appointments");
            }
        }
        return appointmentService.getAppointmentsByPatient(patientId);
    }

    @GetMapping("/doctor/{doctorId}")
    public List<AppointmentDTO> getAppointmentsByDoctor(@PathVariable Long doctorId) {
        // Patients cannot view appointments by doctor
        if (SecurityUtils.isPatient()) {
            throw new RuntimeException("Unauthorized: Patients cannot view doctor appointments");
        }
        return appointmentService.getAppointmentsByDoctor(doctorId);
    }

    @PutMapping("/{id}/reschedule")
    public AppointmentDTO rescheduleAppointment(@PathVariable Long id, @RequestBody Map<String, String> body) {
        AppointmentDTO appointment = appointmentService.getAppointmentById(id);
        
        // Patients can only reschedule their own appointments
        if (SecurityUtils.isPatient()) {
            Long currentPatientId = SecurityUtils.getCurrentUserId();
            if (!appointment.getPatientId().equals(currentPatientId)) {
                throw new RuntimeException("Unauthorized: You can only reschedule your own appointments");
            }
        }
        
        return appointmentService.rescheduleAppointment(id, 
                java.time.LocalDate.parse(body.get("date")), 
                java.time.LocalTime.parse(body.get("time")));
    }

    @PutMapping("/{id}/cancel")
    public AppointmentDTO cancelAppointment(@PathVariable Long id) {
        AppointmentDTO appointment = appointmentService.getAppointmentById(id);
        
        // Patients can only cancel their own appointments
        if (SecurityUtils.isPatient()) {
            Long currentPatientId = SecurityUtils.getCurrentUserId();
            if (!appointment.getPatientId().equals(currentPatientId)) {
                throw new RuntimeException("Unauthorized: You can only cancel your own appointments");
            }
        }
        
        return appointmentService.cancelAppointment(id);
    }

    @PutMapping("/{id}/complete")
    public AppointmentDTO completeAppointment(@PathVariable Long id) {
        return appointmentService.completeAppointment(id);
    }

    @PutMapping("/{id}")
    public AppointmentDTO updateAppointment(@PathVariable Long id, @Valid @RequestBody AppointmentDTO dto) {
        return appointmentService.updateAppointment(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        if (!SecurityUtils.isAdmin()) {
            throw new IllegalStateException("Unauthorized: Only administrators are authorized to delete clinical appointments.");
        }
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }
}
