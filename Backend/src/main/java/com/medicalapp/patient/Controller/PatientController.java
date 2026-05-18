package com.medicalapp.patient.controller;

import com.medicalapp.patient.dto.PatientDTO;
import com.medicalapp.patient.service.IPatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public List<PatientDTO> getAllPatients() {
        return patientService.getAllPatients();
    }

    @GetMapping("/{id}")
    public PatientDTO getPatientById(@PathVariable Long id) {
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
        return patientService.updatePatient(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}