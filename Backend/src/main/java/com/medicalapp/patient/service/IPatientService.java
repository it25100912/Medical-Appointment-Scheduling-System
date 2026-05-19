package com.medicalapp.patient.service;

import com.medicalapp.patient.dto.PatientDTO;
import java.util.List;

public interface IPatientService {

    PatientDTO registerPatient(PatientDTO dto);

    List<PatientDTO> getAllPatients();

    PatientDTO getPatientById(Long id);

    PatientDTO getPatientByNic(String nic);

    PatientDTO getPatientByPhone(String phone);

    PatientDTO updatePatient(Long id, PatientDTO dto);

    void deletePatient(Long id);
}
