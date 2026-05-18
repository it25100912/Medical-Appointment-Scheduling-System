package com.medicalapp.patient.controller;

import com.medicalapp.patient.dto.PatientDTO;
import com.medicalapp.patient.service.IPatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.medicalapp.common.util.SecurityUtils;
import com.medicalapp.common.exception.UnauthorizedException;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PatientController {

    private final IPatientService patientService;

    @PostMapping
    public ResponseEntity<PatientDTO> registerPatient(@Valid @RequestBody PatientDTO dto) {
        return new ResponseEntity<>(patientService.registerPatient(dto), HttpStatus.CREATED);
    }

    @GetMapping
    @GetMapping
    public List<PatientDTO> getAllPatients() {

        // Patients can only view their own details
        if (SecurityUtils.isPatient()) {

            Long patientId = SecurityUtils.getCurrentUserId();

            return List.of(patientService.getPatientById(patientId));
        }

        // Admins and doctors can see all patients
        return patientService.getAllPatients();
    }

    @GetMapping("/{id}")
    public PatientDTO getPatientById(@PathVariable Long id) {

        // Patients can only view their own details
        if (SecurityUtils.isPatient()) {

            Long currentPatientId = SecurityUtils.getCurrentUserId();

            if (!id.equals(currentPatientId)) {
                throw new UnauthorizedException(
                        "Unauthorized: You can only access your own information");
            }
        }

        return patientService.getPatientById(id);
    }

    @GetMapping("/nic/{nic}")
    public PatientDTO getPatientByNic(@PathVariable String nic) {
        return patientService.getPatientByNic(nic);
    }

    @GetMapping("/phone/{phone}")
    public PatientDTO getPatientByPhone(@PathVariable String phone) {
        return patientService.getPatientByPhone(phone);
    }

    @PutMapping("/{id}")
    public PatientDTO updatePatient(@PathVariable Long id,
                                    @Valid @RequestBody PatientDTO dto) {

        // Patients can only update their own details
        if (SecurityUtils.isPatient()) {

            Long currentPatientId = SecurityUtils.getCurrentUserId();

            if (!id.equals(currentPatientId)) {
                throw new UnauthorizedException(
                        "Unauthorized: You can only update your own information");
            }
        }

        return patientService.updatePatient(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {

        // Only admins can delete patients
        if (SecurityUtils.isPatient() || SecurityUtils.isDoctor()) {

            throw new UnauthorizedException(
                    "Unauthorized: Only admins can delete patients");
        }

        patientService.deletePatient(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<PatientDTO> getMyProfile() {

        Long patientId = SecurityUtils.getCurrentUserId();

        if (patientId == null) {
            throw new UnauthorizedException("Unauthorized: User not authenticated");
        }

        PatientDTO patient = patientService.getPatientById(patientId);

        return ResponseEntity.ok(patient);
    }

}