package com.medicalapp.medicalrecord.service;

import com.medicalapp.medicalrecord.dto.MedicalRecordDTO;
import java.util.List;

public interface IMedicalRecordService {

    MedicalRecordDTO addRecord(MedicalRecordDTO dto);

    List<MedicalRecordDTO> getAllRecords();

    MedicalRecordDTO getRecordById(Long id);

    List<MedicalRecordDTO> getRecordsByPatient(Long patientId);

    List<MedicalRecordDTO> getRecordsByDoctor(Long doctorId);

    String getRecordSummary(Long id);

    MedicalRecordDTO updateRecord(Long id, MedicalRecordDTO dto, Long doctorId);

    void deleteRecord(Long id);
}
