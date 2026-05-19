package com.medicalapp.medicalrecord.service;

import com.medicalapp.medicalrecord.dto.MedicalRecordDTO;
import com.medicalapp.medicalrecord.entity.*;
import com.medicalapp.doctor.repository.FileDoctorRepository;
import com.medicalapp.patient.repository.FilePatientRepository;
import com.medicalapp.medicalrecord.repository.FileMedicalRecordRepository;
import com.medicalapp.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MedicalRecordService implements IMedicalRecordService {

    private final FileMedicalRecordRepository medicalRecordRepository;
    private final FilePatientRepository patientRepository;
    private final FileDoctorRepository doctorRepository;

    @Override
    public MedicalRecordDTO addRecord(MedicalRecordDTO dto) {
        PrescriptionRecord record = new PrescriptionRecord();
        
         // Find and assign patient
        record.setPatient(patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found")));
        record.setDoctor(doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found")));
        
        record.setRecordDate(dto.getRecordDate() != null ? dto.getRecordDate() : LocalDate.now());
        record.setDiagnosis(dto.getDiagnosis());
        
        String prescription = dto.getMedicineName() != null ? dto.getMedicineName() : "";
        if (dto.getDosage() != null && !dto.getDosage().isEmpty()) prescription += " (" + dto.getDosage() + ")";
        record.setPrescription(prescription);
        
        record.setNotes(dto.getSummary());
        record.setRecordType(MedicalRecord.RecordType.PRESCRIPTION);

        MedicalRecord saved = medicalRecordRepository.save(record);
        dto.setId(saved.getId());
        return dto;
    }

    @Override
    public List<MedicalRecordDTO> getAllRecords() {
        return medicalRecordRepository.findAll().stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public MedicalRecordDTO getRecordById(Long id) {
        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found"));
        return mapEntityToDto(record);
    }

    @Override
    public List<MedicalRecordDTO> getRecordsByPatient(Long patientId) {
        return medicalRecordRepository.findByPatientId(patientId).stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicalRecordDTO> getRecordsByDoctor(Long doctorId) {
        return medicalRecordRepository.findByDoctorId(doctorId).stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public String getRecordSummary(Long id) {
        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found"));
        return record.getRecordSummary();
    }

    @Override
    public MedicalRecordDTO updateRecord(Long id, MedicalRecordDTO dto, Long doctorId) {
        MedicalRecord existing = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found"));
        
        if (dto.getPatientId() != null) {
            existing.setPatient(patientRepository.findById(dto.getPatientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Patient not found")));
        }
        if (dto.getDoctorId() != null) {
            existing.setDoctor(doctorRepository.findById(dto.getDoctorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor not found")));
        }
        if (dto.getRecordDate() != null) existing.setRecordDate(dto.getRecordDate());
        if (dto.getDiagnosis() != null) existing.setDiagnosis(dto.getDiagnosis());
        
        String prescription = dto.getMedicineName() != null ? dto.getMedicineName() : "";
        if (dto.getDosage() != null && !dto.getDosage().isEmpty()) prescription += " (" + dto.getDosage() + ")";
        if (!prescription.isEmpty()) existing.setPrescription(prescription);
        
        if (dto.getSummary() != null) existing.setNotes(dto.getSummary());

        return mapEntityToDto(medicalRecordRepository.save(existing));
    }

    @Override
    public void deleteRecord(Long id) {

         // Check whether record exists
        if (!medicalRecordRepository.existsById(id)) {
            throw new ResourceNotFoundException("Record not found");
        }
        medicalRecordRepository.deleteById(id);
    }

     // Convert MedicalRecord entity into DTO
    private MedicalRecordDTO mapEntityToDto(MedicalRecord r) {
        MedicalRecordDTO dto = new MedicalRecordDTO();
        dto.setId(r.getId());
        
        // Set patient ID
        if (r.getPatient() != null) {
            dto.setPatientId(r.getPatient().getId());
        } else if (r.getPatientId() != null) {
            dto.setPatientId(r.getPatientId());
        }
        
         // Set doctor ID
        if (r.getDoctor() != null) {
            dto.setDoctorId(r.getDoctor().getId());
        } else if (r.getDoctorId() != null) {
            dto.setDoctorId(r.getDoctorId());
        }
        
        dto.setDiagnosis(r.getDiagnosis());
        dto.setMedicineName(r.getPrescription());
        dto.setSummary(r.getNotes());
        dto.setRecordDate(r.getRecordDate());
        dto.setRecordType(r.getRecordType());
        return dto;
    }
}
