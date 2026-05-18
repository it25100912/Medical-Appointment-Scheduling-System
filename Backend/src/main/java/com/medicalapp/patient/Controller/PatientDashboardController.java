package com.medicalapp.patient.controller;

import com.medicalapp.patient.dto.PatientDTO;
import com.medicalapp.patient.service.IPatientService;
import com.medicalapp.appointment.dto.AppointmentDTO;
import com.medicalapp.appointment.service.IAppointmentService;
import com.medicalapp.medicalrecord.dto.MedicalRecordDTO;
import com.medicalapp.medicalrecord.service.IMedicalRecordService;
import com.medicalapp.billingandpayment.dto.BillingDTO;
import com.medicalapp.billingandpayment.service.IBillingService;
import com.medicalapp.common.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PatientDashboardController {

    private final IPatientService patientService;
    private final IAppointmentService appointmentService;
    private final IMedicalRecordService medicalRecordService;
    private final IBillingService billingService;

    @GetMapping("/my-profile")
    public ResponseEntity<PatientDTO> getMyProfile() {
        // Get the logged-in patient's profile
        Long patientId = SecurityUtils.getCurrentUserId();
        if (patientId == null) {
            throw new RuntimeException("Unauthorized: User not authenticated");
        }
        PatientDTO patient = patientService.getPatientById(patientId);
        return ResponseEntity.ok(patient);
    }

    @GetMapping("/my-appointments")
    public ResponseEntity<List<AppointmentDTO>> getMyAppointments() {
        // Get the logged-in patient's appointments
        Long patientId = SecurityUtils.getCurrentUserId();
        if (patientId == null) {
            throw new RuntimeException("Unauthorized: User not authenticated");
        }
        List<AppointmentDTO> appointments = appointmentService.getAppointmentsByPatient(patientId);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/my-medical-records")
    public ResponseEntity<List<MedicalRecordDTO>> getMyMedicalRecords() {
        // Get the logged-in patient's medical records
        Long patientId = SecurityUtils.getCurrentUserId();
        if (patientId == null) {
            throw new RuntimeException("Unauthorized: User not authenticated");
        }
        List<MedicalRecordDTO> records = medicalRecordService.getRecordsByPatient(patientId);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/my-billing")
    public ResponseEntity<List<BillingDTO>> getMyBillingInfo() {
        // Get the logged-in patient's billing information
        Long patientId = SecurityUtils.getCurrentUserId();
        if (patientId == null) {
            throw new RuntimeException("Unauthorized: User not authenticated");
        }
        List<BillingDTO> bills = billingService.getBillsByPatient(patientId);
        return ResponseEntity.ok(bills);
    }

    @GetMapping("/complete-dashboard")
    public ResponseEntity<PatientDashboardDTO> getCompleteDashboard() {
        // Get all dashboard information for the logged-in patient
        Long patientId = SecurityUtils.getCurrentUserId();
        if (patientId == null) {
            throw new RuntimeException("Unauthorized: User not authenticated");
        }

        PatientDTO profile = patientService.getPatientById(patientId);
        List<AppointmentDTO> appointments = appointmentService.getAppointmentsByPatient(patientId);
        List<MedicalRecordDTO> medicalRecords = medicalRecordService.getRecordsByPatient(patientId);
        List<BillingDTO> billing = billingService.getBillsByPatient(patientId);

        PatientDashboardDTO dashboard = new PatientDashboardDTO(
                profile,
                appointments,
                medicalRecords,
                billing
        );

        return ResponseEntity.ok(dashboard);
    }

    @Data
    public static class PatientDashboardDTO {
        private PatientDTO profile;
        private List<AppointmentDTO> appointments;
        private List<MedicalRecordDTO> medicalRecords;
        private List<BillingDTO> billing;

        public PatientDashboardDTO(PatientDTO profile, List<AppointmentDTO> appointments,
                                   List<MedicalRecordDTO> medicalRecords, List<BillingDTO> billing) {
            this.profile = profile;
            this.appointments = appointments;
            this.medicalRecords = medicalRecords;
            this.billing = billing;
        }
    }
}
