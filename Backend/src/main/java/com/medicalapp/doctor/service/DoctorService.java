package com.medicalapp.doctor.service;

import com.medicalapp.doctor.dto.DoctorDTO;
import com.medicalapp.doctor.entity.Doctor;
import com.medicalapp.doctor.entity.GeneralDoctor;
import com.medicalapp.doctor.entity.SpecialistDoctor;
import com.medicalapp.doctor.repository.FileDoctorRepository;
import com.medicalapp.common.exception.DuplicateEntryException;
import com.medicalapp.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DoctorService implements IDoctorService {

    private final FileDoctorRepository doctorRepository;

    @Override
    public DoctorDTO addDoctor(DoctorDTO dto) {
        Doctor doctor = new GeneralDoctor();
        mapDtoToEntity(dto, doctor);
        Doctor saved = doctorRepository.save(doctor);
        return mapEntityToDto(saved);
    }

    @Override
    public List<DoctorDTO> getAllDoctors() {
        return doctorRepository.findAll().stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public DoctorDTO getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + id));
        return mapEntityToDto(doctor);
    }

    @Override
    public List<DoctorDTO> getDoctorsBySpecialization(String spec) {
        return doctorRepository.findAll().stream()
                .filter(d -> d.getSpecialization().equalsIgnoreCase(spec))
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<DoctorDTO> getDoctorsByAvailableDay(String day) {
        return doctorRepository.findAll().stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public DoctorDTO updateDoctor(Long id, DoctorDTO dto) {
        Doctor existing = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + id));

        mapDtoToEntity(dto, existing);
        if (existing instanceof SpecialistDoctor && dto.getSpecialistArea() != null) {
            ((SpecialistDoctor) existing).setSpecialistArea(dto.getSpecialistArea());
        }

        return mapEntityToDto(doctorRepository.save(existing));
    }

    @Override
    public void deleteDoctor(Long id) {
        doctorRepository.deleteById(id);
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
