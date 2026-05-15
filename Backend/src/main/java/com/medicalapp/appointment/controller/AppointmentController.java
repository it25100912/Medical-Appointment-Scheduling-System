package com.medicalapp.appointment.controller;

import com.medicalapp.appointment.dto.AppointmentDTO;
import com.medicalapp.appointment.service.IAppointmentService;
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
        return appointmentService.getAllAppointments();
    }

    @GetMapping("/{id}")
    public AppointmentDTO getAppointmentById(@PathVariable Long id) {
        return appointmentService.getAppointmentById(id);
    }

    @GetMapping("/patient/{patientId}")
    public List<AppointmentDTO> getAppointmentsByPatient(@PathVariable Long patientId) {
        return appointmentService.getAppointmentsByPatient(patientId);
    }

    @GetMapping("/doctor/{doctorId}")
    public List<AppointmentDTO> getAppointmentsByDoctor(@PathVariable Long doctorId) {
        return appointmentService.getAppointmentsByDoctor(doctorId);
    }

 @PutMapping("/{id}/reschedule")
    public AppointmentDTO rescheduleAppointment(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return appointmentService.rescheduleAppointment(id, 
                java.time.LocalDate.parse(body.get("date")), 
                java.time.LocalTime.parse(body.get("time")));
    }

    @PutMapping("/{id}/cancel")
    public AppointmentDTO cancelAppointment(@PathVariable Long id) {
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
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }
}
