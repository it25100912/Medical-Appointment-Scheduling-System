package com.medicalapp.patient.service;

import com.medicalapp.patient.dto.PatientDTO;
import com.medicalapp.patient.entity.InPatient;
import com.medicalapp.patient.entity.OutPatient;
import com.medicalapp.patient.entity.Patient;
import com.medicalapp.patient.repository.FilePatientRepository;
import com.medicalapp.auth.entity.User;
import com.medicalapp.auth.repository.FileUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientService implements IPatientService {

    private final FilePatientRepository patientRepository;
    private final FileUserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    @Override
    public PatientDTO registerPatient(PatientDTO dto) {

        Patient patient = mapDtoToEntity(dto);

        Patient saved = patientRepository.save(patient);

        // Create corresponding User record
        if (dto.getPassword() != null &&
                !dto.getPassword().isEmpty() &&
                saved.getId() != null) {

            User user = new User();

            user.setId(saved.getId());
            user.setEmail(saved.getEmail());
            user.setPassword(
                    passwordEncoder.encode(dto.getPassword())
            );
            user.setName(saved.getName());
            user.setNic(saved.getNic());
            user.setPhone(saved.getPhone());
            user.setRole(User.Role.PATIENT);

            userRepository.save(user);
        }

        return mapEntityToDto(saved);
    }

    private Patient mapDtoToEntity(PatientDTO dto) {

        Patient p;

        if (dto.getPatientType() ==
                Patient.PatientType.INPATIENT) {

            p = new InPatient();

        } else {

            p = new OutPatient();
        }

        p.setName(dto.getName());
        p.setEmail(dto.getEmail());
        p.setPhone(dto.getPhone());
        p.setNic(dto.getNic());
        p.setAddress(dto.getAddress());
        p.setBloodGroup(dto.getBloodGroup());
        p.setDateOfBirth(dto.getDateOfBirth());
        p.setMedicalHistory(dto.getMedicalHistory());
        p.setPatientType(dto.getPatientType());

        // Hash password if provided
        if (dto.getPassword() != null &&
                !dto.getPassword().trim().isEmpty()) {

            p.setPassword(
                    passwordEncoder.encode(dto.getPassword())
            );
        }

        return p;
    }

    private PatientDTO mapEntityToDto(Patient p) {

        PatientDTO dto = new PatientDTO();

        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setEmail(p.getEmail());
        dto.setPhone(p.getPhone());
        dto.setNic(p.getNic());
        dto.setAddress(p.getAddress());
        dto.setBloodGroup(p.getBloodGroup());
        dto.setDateOfBirth(p.getDateOfBirth());
        dto.setMedicalHistory(p.getMedicalHistory());
        dto.setPatientType(p.getPatientType());

        return dto;
    }
}