package com.medicalapp.doctor.service;

import com.medicalapp.doctor.dto.DoctorDTO;
import com.medicalapp.doctor.entity.Doctor;
import com.medicalapp.doctor.entity.GeneralDoctor;
import com.medicalapp.doctor.entity.SpecialistDoctor;
import com.medicalapp.doctor.repository.FileDoctorRepository;
import com.medicalapp.auth.entity.User;
import com.medicalapp.auth.repository.FileUserRepository;
import com.medicalapp.common.exception.DuplicateEntryException;
import com.medicalapp.common.exception.ResourceNotFoundException;
import com.medicalapp.appointment.repository.FileAppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DoctorService implements IDoctorService {

    private final FileDoctorRepository doctorRepository;
    private final FileUserRepository userRepository;
    private final FileAppointmentRepository appointmentRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public DoctorDTO addDoctor(DoctorDTO dto) {
        if (doctorRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new DuplicateEntryException("Doctor with this email already exists");
        }
        Doctor doctor = dto.getDoctorType() == Doctor.DoctorType.SPECIALIST ? new SpecialistDoctor() : new GeneralDoctor();
        mapDtoToEntity(dto, doctor);
        Doctor saved = doctorRepository.save(doctor);
        
        // Create corresponding User record for authentication
        if (dto.getPassword() != null && !dto.getPassword().isEmpty() && saved.getId() != null) {
            User user = new User();
            user.setId(saved.getId());
            user.setEmail(saved.getEmail());
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            user.setName(saved.getName());
            user.setLicenseNumber(saved.getLicenseNumber());
            user.setPhone(saved.getPhone());
            user.setRole(User.Role.DOCTOR);
            userRepository.save(user);
        }
        
        return mapEntityToDto(saved);
    }

    @Override
    public List<DoctorDTO> getAllDoctors() {
        List<DoctorDTO> list = doctorRepository.findAll().stream()
            .map(this::mapEntityToDto)
            .collect(Collectors.toList());

        // Include users from users.txt who are doctors but may not exist in doctors.txt
        userRepository.findAll().stream()
            .filter(u -> u.getRole() == com.medicalapp.auth.entity.User.Role.DOCTOR)
            .forEach(u -> {
                boolean exists = doctorRepository.findById(u.getId()).isPresent() ||
                    doctorRepository.findByEmail(u.getEmail()).isPresent();
                if (!exists) {
                DoctorDTO dto = new DoctorDTO();
                dto.setId(u.getId());
                dto.setName(u.getName());
                dto.setEmail(u.getEmail());
                dto.setPhone(u.getPhone());
                dto.setLicenseNumber(u.getLicenseNumber());
                list.add(dto);
                }
            });

        return list;
    }

    @Override
    public DoctorDTO getDoctorById(Long id) {
        return doctorRepository.findById(id)
            .map(this::mapEntityToDto)
            .orElseGet(() -> userRepository.findById(id)
                .filter(u -> u.getRole() == User.Role.DOCTOR)
                .map(u -> {
                    DoctorDTO dto = new DoctorDTO();
                    dto.setId(u.getId());
                    dto.setName(u.getName());
                    dto.setEmail(u.getEmail());
                    dto.setPhone(u.getPhone());
                    dto.setLicenseNumber(u.getLicenseNumber());
                    return dto;
                }).orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + id)));
    }

    @Override
    public List<DoctorDTO> getDoctorsBySpecialization(String spec) {
        return doctorRepository.findBySpecialization(spec).stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<DoctorDTO> getDoctorsByAvailableDay(String day) {
        return doctorRepository.findByAvailableDaysContaining(day).stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public DoctorDTO updateDoctor(Long id, DoctorDTO dto) {
        Doctor existing = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + id));

        mapDtoToEntity(dto, existing);
        
        // Update password if provided
        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(dto.getPassword()));
            // Also update User record password
            userRepository.findByEmail(existing.getEmail()).ifPresent(user -> {
                user.setPassword(passwordEncoder.encode(dto.getPassword()));
                userRepository.save(user);
            });
        }
        
        return mapEntityToDto(doctorRepository.save(existing));
    }

    @Override
    public void deleteDoctor(Long id) {
        if (!appointmentRepository.findByDoctorId(id).isEmpty()) {
            throw new IllegalStateException("Cannot delete doctor with active appointments associated.");
        }
        doctorRepository.deleteById(id);
        userRepository.deleteById(id);
    }

    @Override
    public double getCalculatedFee(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + id));
        return doctor.calculateConsultationFee();
    }

    private void mapDtoToEntity(DoctorDTO dto, Doctor doctor) {
        doctor.setName(dto.getName());
        doctor.setEmail(dto.getEmail());
        doctor.setPhone(dto.getPhone());
        doctor.setSpecialization(dto.getSpecialization());
        doctor.setDoctorType(dto.getDoctorType());
        doctor.setConsultationFee(dto.getConsultationFee());
        doctor.setAvailableDays(dto.getAvailableDays());
        doctor.setAvailableFrom(dto.getAvailableFrom());
        doctor.setAvailableTo(dto.getAvailableTo());
        doctor.setLicenseNumber(dto.getLicenseNumber());
        doctor.setExperience(dto.getExperience());
        
        // Hash password if provided
        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            doctor.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        
        if (doctor instanceof SpecialistDoctor && dto.getSpecialistArea() != null) {
            ((SpecialistDoctor) doctor).setSpecialistArea(dto.getSpecialistArea());
        }
    }

    private DoctorDTO mapEntityToDto(Doctor doctor) {
        DoctorDTO dto = new DoctorDTO();
        dto.setId(doctor.getId());
        dto.setName(doctor.getName());
        dto.setEmail(doctor.getEmail());
        dto.setPhone(doctor.getPhone());
        dto.setSpecialization(doctor.getSpecialization());
        dto.setDoctorType(doctor.getDoctorType());
        dto.setConsultationFee(doctor.getConsultationFee());
        dto.setAvailableDays(doctor.getAvailableDays());
        dto.setAvailableFrom(doctor.getAvailableFrom());
        dto.setAvailableTo(doctor.getAvailableTo());
        dto.setLicenseNumber(doctor.getLicenseNumber());
        dto.setExperience(doctor.getExperience());
        if (doctor instanceof SpecialistDoctor) {
            dto.setSpecialistArea(((SpecialistDoctor) doctor).getSpecialistArea());
        }
        return dto;
    }
}
