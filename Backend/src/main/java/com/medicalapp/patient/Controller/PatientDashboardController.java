package com.medicalapp.patient.controller;

import com.medicalapp.patient.dto.PatientDTO;
import com.medicalapp.patient.service.IPatientService;

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




}
