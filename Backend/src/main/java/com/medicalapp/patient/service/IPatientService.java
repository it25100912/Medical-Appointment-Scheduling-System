package com.medicalapp.patient.service;

import com.medicalapp.patient.dto.PatientDTO;
import java.util.List;

// OOP CONCEPT: ABSTRACTION - Interface defines the system operation blueprint, hiding storage/implementation details from controller routes.
public interface IPatientService {

    PatientDTO registerPatient(PatientDTO dto);

    List<PatientDTO> getAllPatients();

    PatientDTO getPatientById(Long id);

    PatientDTO getPatientByNic(String nic);

    PatientDTO getPatientByPhone(String phone);

    PatientDTO updatePatient(Long id, PatientDTO dto);

    void deletePatient(Long id);
}
