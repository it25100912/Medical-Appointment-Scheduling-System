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
        return medicalRecordService.getRecordById(id);
    }

    @GetMapping("/patient/{patientId}")
    public List<MedicalRecordDTO> getRecordsByPatient(@PathVariable Long patientId) {
        return medicalRecordService.getRecordsByPatient(patientId);
    }

    @GetMapping("/doctor/{doctorId}")
    public List<MedicalRecordDTO> getRecordsByDoctor(@PathVariable Long doctorId) {
        return medicalRecordService.getRecordsByDoctor(doctorId);
    }

    @GetMapping("/{id}/summary")
    public String getRecordSummary(@PathVariable Long id) {
        return medicalRecordService.getRecordSummary(id);
    }

    @PutMapping("/{id}")
    public MedicalRecordDTO updateRecord(@PathVariable Long id, @Valid @RequestBody MedicalRecordDTO dto) {
        return medicalRecordService.updateRecord(id, dto, dto.getDoctorId());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecord(@PathVariable Long id) {
        medicalRecordService.deleteRecord(id);
        return ResponseEntity.noContent().build();
    }
}
