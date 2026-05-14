package com.medicalapp.medicalrecord.service;

import com.medicalapp.medicalrecord.dto.MedicalRecordDTO;
import com.medicalapp.medicalrecord.entity.*;
import com.medicalapp.doctor.entity.Doctor;
import com.medicalapp.patient.repository.FilePatientRepository;
import com.medicalapp.doctor.repository.FileDoctorRepository;
import com.medicalapp.medicalrecord.repository.FileMedicalRecordRepository;
import com.medicalapp.common.exception.ResourceNotFoundException;
import com.medicalapp.common.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        MedicalRecord record = new PrescriptionRecord();
        record.setPatientId(dto.getPatientId());
        record.setDoctorId(dto.getDoctorId());
        record.setDiagnosis(dto.getDiagnosis());
        
        String prescription = dto.getMedicineName() != null ? dto.getMedicineName() : "";
        if (dto.getDosage() != null) prescription += " (" + dto.getDosage() + ")";
        record.setPrescription(prescription);
        
        record.setNotes(dto.getSummary());

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
        return medicalRecordRepository.findAll().stream()
                .filter(r -> r.getPatientId().equals(patientId))
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicalRecordDTO> getRecordsByDoctor(Long doctorId) {
        return medicalRecordRepository.findAll().stream()
                .filter(r -> r.getDoctorId().equals(doctorId))
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
        
        if (dto.getPatientId() != null) existing.setPatientId(dto.getPatientId());
        if (dto.getDoctorId() != null) existing.setDoctorId(dto.getDoctorId());
        if (dto.getDiagnosis() != null) existing.setDiagnosis(dto.getDiagnosis());
        
        String prescription = dto.getMedicineName() != null ? dto.getMedicineName() : "";
        if (dto.getDosage() != null && !dto.getDosage().isEmpty()) prescription += " (" + dto.getDosage() + ")";
        if (!prescription.isEmpty()) existing.setPrescription(prescription);
        
        if (dto.getSummary() != null) existing.setNotes(dto.getSummary());

        return mapEntityToDto(medicalRecordRepository.save(existing));
    }

    @Override
    public void deleteRecord(Long id) {
        if (!medicalRecordRepository.existsById(id)) {
            throw new ResourceNotFoundException("Record not found");
        }
        medicalRecordRepository.deleteById(id);
    }

    private MedicalRecordDTO mapEntityToDto(MedicalRecord r) {
        MedicalRecordDTO dto = new MedicalRecordDTO();
        dto.setId(r.getId());
        dto.setPatientId(r.getPatientId());
        dto.setDoctorId(r.getDoctorId());
        dto.setDiagnosis(r.getDiagnosis());
        dto.setMedicineName(r.getPrescription());
        dto.setSummary(r.getNotes());
        return dto;
    }
}
