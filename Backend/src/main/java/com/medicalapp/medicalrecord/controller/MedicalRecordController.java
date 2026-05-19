package com.medicalapp.medicalrecord.controller;

import com.medicalapp.medicalrecord.dto.MedicalRecordDTO;
import com.medicalapp.medicalrecord.service.IMedicalRecordService;
import com.medicalapp.common.util.SecurityUtils;
import com.medicalapp.common.exception.UnauthorizedException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medical-records")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MedicalRecordController {

    private final IMedicalRecordService medicalRecordService;

    @PostMapping
    public ResponseEntity<MedicalRecordDTO> addRecord(@Valid @RequestBody MedicalRecordDTO dto) {
        if (SecurityUtils.isPatient()) {
            throw new UnauthorizedException("Unauthorized: Patients cannot create medical records");
        }
        return new ResponseEntity<>(medicalRecordService.addRecord(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public List<MedicalRecordDTO> getAllRecords() {
        // Patients can only view their own medical records
        if (SecurityUtils.isPatient()) {
            Long patientId = SecurityUtils.getCurrentUserId();
            return medicalRecordService.getRecordsByPatient(patientId);
        }
        // Doctors and admins can see all records
        return medicalRecordService.getAllRecords();
    }

    @GetMapping("/{id}")
    public MedicalRecordDTO getRecordById(@PathVariable Long id) {
        MedicalRecordDTO record = medicalRecordService.getRecordById(id);
        
        // Patients can only view their own medical records
        if (SecurityUtils.isPatient()) {
            Long currentPatientId = SecurityUtils.getCurrentUserId();
            if (!record.getPatientId().equals(currentPatientId)) {
                throw new UnauthorizedException("Unauthorized: You can only access your own medical records");
            }
        }
        return record;
    }

    @GetMapping("/patient/{patientId}")
    public List<MedicalRecordDTO> getRecordsByPatient(@PathVariable Long patientId) {
        // Patients can only view their own medical records
        if (SecurityUtils.isPatient()) {
            Long currentPatientId = SecurityUtils.getCurrentUserId();
            if (!patientId.equals(currentPatientId)) {
                throw new UnauthorizedException("Unauthorized: You can only access your own medical records");
            }
        }
        return medicalRecordService.getRecordsByPatient(patientId);
    }

    @GetMapping("/doctor/{doctorId}")
    public List<MedicalRecordDTO> getRecordsByDoctor(@PathVariable Long doctorId) {
        // Patients cannot view records by doctor
        if (SecurityUtils.isPatient()) {
            throw new UnauthorizedException("Unauthorized: Patients cannot view doctor records");
        }
        return medicalRecordService.getRecordsByDoctor(doctorId);
    }

    @GetMapping("/{id}/summary")
    public String getRecordSummary(@PathVariable Long id) {
        MedicalRecordDTO record = medicalRecordService.getRecordById(id);
        
        // Patients can only view their own record summaries
        if (SecurityUtils.isPatient()) {
            Long currentPatientId = SecurityUtils.getCurrentUserId();
            if (!record.getPatientId().equals(currentPatientId)) {
                throw new UnauthorizedException("Unauthorized: You can only access your own medical records");
            }
        }
        return medicalRecordService.getRecordSummary(id);
    }

    @PutMapping("/{id}")
    public MedicalRecordDTO updateRecord(@PathVariable Long id, @Valid @RequestBody MedicalRecordDTO dto) {
        MedicalRecordDTO record = medicalRecordService.getRecordById(id);
        
        // Patients cannot update their own medical records
        if (SecurityUtils.isPatient()) {
            throw new UnauthorizedException("Unauthorized: Patients cannot update medical records");
        }
        return medicalRecordService.updateRecord(id, dto, dto.getDoctorId());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecord(@PathVariable Long id) {
        // Patients cannot delete their own medical records
        if (SecurityUtils.isPatient()) {
            throw new UnauthorizedException("Unauthorized: Patients cannot delete medical records");
        }
        medicalRecordService.deleteRecord(id);
        return ResponseEntity.noContent().build();
    }
}
