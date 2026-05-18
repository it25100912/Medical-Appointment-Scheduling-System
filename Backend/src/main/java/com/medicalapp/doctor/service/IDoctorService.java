package com.medicalapp.doctor.service;

import com.medicalapp.doctor.dto.DoctorDTO;
import java.util.List;

public interface IDoctorService {

    DoctorDTO addDoctor(DoctorDTO dto);

    List<DoctorDTO> getAllDoctors();

    DoctorDTO getDoctorById(Long id);

    List<DoctorDTO> getDoctorsBySpecialization(String spec);

    List<DoctorDTO> getDoctorsByAvailableDay(String day);

    DoctorDTO updateDoctor(Long id, DoctorDTO dto);

    void deleteDoctor(Long id);

    double getCalculatedFee(Long id);
}
