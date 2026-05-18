package com.medicalapp.patient.service;

import com.medicalapp.appointment.repository.FileAppointmentRepository;
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
import com.medicalapp.common.exception.ResourceNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientService implements IPatientService {


    private final FilePatientRepository patientRepository;
    private final FileUserRepository userRepository;
    private final FileAppointmentRepository appointmentRepository;
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

    //Patient Retirival and search
    @Override
    public List<PatientDTO> getAllPatients() {

        List<PatientDTO> list = patientRepository.findAll().stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());

        // Include patient users from users.txt
        userRepository.findAll().stream()
                .filter(u -> u.getRole() == User.Role.PATIENT)
                .forEach(u -> {

                    boolean exists =
                            patientRepository.findById(u.getId()).isPresent()
                                    || patientRepository.findByEmail(u.getEmail()).isPresent();

                    if (!exists) {

                        PatientDTO dto = new PatientDTO();

                        dto.setId(u.getId());
                        dto.setName(u.getName());
                        dto.setEmail(u.getEmail());
                        dto.setPhone(u.getPhone());
                        dto.setNic(u.getNic());

                        list.add(dto);
                    }
                });

        return list;
    }

    @Override
    public PatientDTO getPatientById(Long id) {

        return patientRepository.findById(id)
                .map(this::mapEntityToDto)
                .orElseGet(() -> {

                    return userRepository.findById(id)
                            .filter(u -> u.getRole() == User.Role.PATIENT)
                            .map(u -> {

                                PatientDTO dto = new PatientDTO();

                                dto.setId(u.getId());
                                dto.setName(u.getName());
                                dto.setEmail(u.getEmail());
                                dto.setPhone(u.getPhone());
                                dto.setNic(u.getNic());

                                return dto;

                            }).orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Patient not found"
                                    ));
                });
    }

    @Override
    public PatientDTO getPatientByNic(String nic) {

        return patientRepository.findByNic(nic)
                .map(this::mapEntityToDto)
                .orElseGet(() -> userRepository.findAll().stream()
                        .filter(u ->
                                u.getNic() != null
                                        && u.getNic().equals(nic)
                                        && u.getRole() == User.Role.PATIENT
                        )
                        .findFirst()
                        .map(u -> {

                            PatientDTO dto = new PatientDTO();

                            dto.setId(u.getId());
                            dto.setName(u.getName());
                            dto.setEmail(u.getEmail());
                            dto.setPhone(u.getPhone());
                            dto.setNic(u.getNic());

                            return dto;

                        }).orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Patient not found"
                                )));
    }

    @Override
    public PatientDTO getPatientByPhone(String phone) {

        return patientRepository.findByPhone(phone)
                .map(this::mapEntityToDto)
                .orElseGet(() -> userRepository.findAll().stream()
                        .filter(u ->
                                u.getPhone() != null
                                        && u.getPhone().equals(phone)
                                        && u.getRole() == User.Role.PATIENT
                        )
                        .findFirst()
                        .map(u -> {

                            PatientDTO dto = new PatientDTO();

                            dto.setId(u.getId());
                            dto.setName(u.getName());
                            dto.setEmail(u.getEmail());
                            dto.setPhone(u.getPhone());
                            dto.setNic(u.getNic());

                            return dto;

                        }).orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Patient not found"
                                )));
    }

    @Override
    public PatientDTO updatePatient(Long id, PatientDTO dto) {

        Patient existing =
                patientRepository.findById(id).orElse(null);

        if (existing == null) {

            // Create from users.txt if missing
            User user =
                    userRepository.findById(id).orElse(null);

            if (user == null ||
                    user.getRole() != User.Role.PATIENT) {

                throw new ResourceNotFoundException(
                        "Patient not found"
                );
            }

            Patient p = new OutPatient();

            p.setId(user.getId());
            p.setName(user.getName());
            p.setEmail(user.getEmail());
            p.setPhone(user.getPhone());
            p.setNic(user.getNic());
            p.setPassword(user.getPassword());

            if (dto.getName() != null)
                p.setName(dto.getName());

            if (dto.getEmail() != null)
                p.setEmail(dto.getEmail());

            if (dto.getPhone() != null)
                p.setPhone(dto.getPhone());

            if (dto.getNic() != null)
                p.setNic(dto.getNic());

            if (dto.getAddress() != null)
                p.setAddress(dto.getAddress());

            if (dto.getBloodGroup() != null)
                p.setBloodGroup(dto.getBloodGroup());

            if (dto.getDateOfBirth() != null)
                p.setDateOfBirth(dto.getDateOfBirth());

            if (dto.getMedicalHistory() != null)
                p.setMedicalHistory(dto.getMedicalHistory());

            // Password update
            if (dto.getPassword() != null &&
                    !dto.getPassword().trim().isEmpty()) {

                p.setPassword(
                        passwordEncoder.encode(dto.getPassword())
                );

                user.setPassword(
                        passwordEncoder.encode(dto.getPassword())
                );

                userRepository.save(user);
            }

            Patient saved = patientRepository.save(p);

            return mapEntityToDto(saved);
        }

        existing.setName(dto.getName());
        existing.setEmail(dto.getEmail());
        existing.setPhone(dto.getPhone());
        existing.setNic(dto.getNic());
        existing.setAddress(dto.getAddress());
        existing.setBloodGroup(dto.getBloodGroup());
        existing.setDateOfBirth(dto.getDateOfBirth());
        existing.setMedicalHistory(dto.getMedicalHistory());

        if (dto.getPassword() != null &&
                !dto.getPassword().trim().isEmpty()) {

            existing.setPassword(
                    passwordEncoder.encode(dto.getPassword())
            );

            userRepository.findByEmail(existing.getEmail())
                    .ifPresent(user -> {

                        user.setPassword(
                                passwordEncoder.encode(
                                        dto.getPassword()
                                )
                        );

                        userRepository.save(user);
                    });
        }

        return mapEntityToDto(
                patientRepository.save(existing)
        );
    }

    @Override
    public void deletePatient(Long id) {

        if (!appointmentRepository
                .findByPatientId(id)
                .isEmpty()) {

            throw new IllegalStateException(
                    "Cannot delete patient with active appointments associated."
            );
        }

        patientRepository.deleteById(id);
        userRepository.deleteById(id);
    }
}