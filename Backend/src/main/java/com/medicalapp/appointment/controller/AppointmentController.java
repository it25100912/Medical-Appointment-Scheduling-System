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

   