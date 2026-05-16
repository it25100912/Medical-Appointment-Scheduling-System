package com.medicalapp.doctor.repository;

import com.medicalapp.common.storage.FileStorageUtil;
import com.medicalapp.doctor.entity.Doctor;
import com.medicalapp.doctor.entity.GeneralDoctor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class FileDoctorRepository {
    private final FileStorageUtil<Doctor> storage = new FileStorageUtil<>("doctors.txt");

    public List<Doctor> findAll() {
        return storage.readFromFile(this::mapToDoctor);
    }

    public Optional<Doctor> findById(Long id) {
        return findAll().stream().filter(d -> d.getId().equals(id)).findFirst();
    }

    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }

    public Doctor save(Doctor doctor) {
        List<Doctor> doctors = findAll();
        if (doctor.getId() == null) {
            doctor.setId(System.currentTimeMillis());
            doctors.add(doctor);
        } else {
            doctors = doctors.stream()
                    .map(d -> d.getId().equals(doctor.getId()) ? doctor : d)
                    .collect(Collectors.toList());
        }
        storage.writeToFile(doctors, this::mapToString);
        return doctor;
    }

    public void deleteById(Long id) {
        List<Doctor> doctors = findAll().stream()
                .filter(d -> !d.getId().equals(id))
                .collect(Collectors.toList());
        storage.writeToFile(doctors, this::mapToString);
    }

    private String mapToString(Doctor d) {
        return String.format("%d,%s,%s,%s,%s,%s",
                d.getId(),
                clean(d.getName(), "Unknown"),
                clean(d.getSpecialization(), "General"),
                clean(d.getLicenseNumber(), "N/A"),
                d.getExperience() != null ? d.getExperience().toString() : "0",
                d.getConsultationFee() != null ? d.getConsultationFee().toString() : "0.0");
    }

    private String clean(String input, String def) {
        if (input == null || input.trim().isEmpty()) return def;
        return input.replace(",", " ").replace("\n", " ").replace("\r", " ").trim();
    }

    private Doctor mapToDoctor(String line) {
        String[] parts = line.split(",");
        Doctor d = new GeneralDoctor();
        if (parts.length < 2) return null;

        try {
            d.setId(Long.parseLong(parts[0]));
            d.setName(parts[1]);
            if (parts.length > 2 && !"N/A".equals(parts[2])) d.setSpecialization(parts[2]);
            if (parts.length > 3 && !"N/A".equals(parts[3])) d.setLicenseNumber(parts[3]);
            if (parts.length > 4) {
                try {
                    d.setExperience(Integer.parseInt(parts[4]));
                } catch (NumberFormatException e) {
                    d.setExperience(0);
                }
            }
            if (parts.length > 5) {
                try {
                    d.setConsultationFee(Double.parseDouble(parts[5]));
                } catch (NumberFormatException e) {
                    d.setConsultationFee(0.0);
                }
            }
            return d;
        } catch (Exception e) {
            return null;
        }
    }
}


