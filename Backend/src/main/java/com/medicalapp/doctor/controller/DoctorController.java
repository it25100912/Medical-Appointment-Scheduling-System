package com.medicalapp.doctor.controller;

import com.medicalapp.doctor.dto.DoctorDTO;
import com.medicalapp.doctor.service.IDoctorService;
import com.medicalapp.common.util.SecurityUtils;
import com.medicalapp.common.exception.UnauthorizedException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DoctorController {

    private final IDoctorService doctorService;

    @PostMapping
    public ResponseEntity<DoctorDTO> addDoctor(@Valid @RequestBody DoctorDTO dto) {
        return new ResponseEntity<>(doctorService.addDoctor(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public List<DoctorDTO> getAllDoctors() {
        return doctorService.getAllDoctors();
    }

    @GetMapping("/me")
    public ResponseEntity<DoctorDTO> getMyProfile() {
        Long doctorId = SecurityUtils.getCurrentUserId();
        if (doctorId == null) {
            throw new UnauthorizedException("Unauthorized: User not authenticated");
        }
        DoctorDTO doctor = doctorService.getDoctorById(doctorId);
        return ResponseEntity.ok(doctor);
    }

    @GetMapping("/{id}")
    public DoctorDTO getDoctorById(@PathVariable Long id) {
        return doctorService.getDoctorById(id);
    }

    @GetMapping("/specialization/{spec}")
    public List<DoctorDTO> getDoctorsBySpecialization(@PathVariable String spec) {
        return doctorService.getDoctorsBySpecialization(spec);
    }

    @GetMapping("/available/{day}")
    public List<DoctorDTO> getDoctorsByAvailableDay(@PathVariable String day) {
        return doctorService.getDoctorsByAvailableDay(day);
    }

    @GetMapping("/{id}/fee")
    public Double getCalculatedFee(@PathVariable Long id) {
        return doctorService.getCalculatedFee(id);
    }

    @PutMapping("/{id}")
    public DoctorDTO updateDoctor(@PathVariable Long id, @Valid @RequestBody DoctorDTO dto) {
        return doctorService.updateDoctor(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }
}
