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
            boolean updated = false;
            for (int i = 0; i < doctors.size(); i++) {
                if (doctors.get(i).getId().equals(doctor.getId())) {
                    doctors.set(i, doctor);
                    updated = true;
                    break;
                }
            }
            if (!updated) {
                doctors.add(doctor);
            }
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

    public Optional<Doctor> findByEmail(String email) {
        return findAll().stream()
                .filter(d -> d.getEmail() != null && d.getEmail().equals(email))
                .findFirst();
    }

    public List<Doctor> findBySpecialization(String specialization) {
        return findAll().stream()
                .filter(d -> d.getSpecialization() != null && d.getSpecialization().equals(specialization))
                .collect(Collectors.toList());
    }

    public List<Doctor> findByAvailableDaysContaining(String day) {
        return findAll().stream()
                .filter(d -> d.getAvailableDays() != null && d.getAvailableDays().contains(day))
                .collect(Collectors.toList());
    }

    private String mapToString(Doctor d) {
        return String.format("%d,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                d.getId(),
                clean(d.getName(), "Unknown"),
                clean(d.getEmail(), "N/A"),
                clean(d.getPhone(), "N/A"),
                clean(d.getSpecialization(), "General"),
                clean(d.getLicenseNumber(), "N/A"),
                d.getExperience() != null ? d.getExperience().toString() : "0",
                d.getConsultationFee() != null ? d.getConsultationFee().toString() : "0.0",
                clean(d.getAvailableDays(), "N/A"),
                d.getAvailableFrom() != null ? d.getAvailableFrom().toString() : "N/A",
                d.getAvailableTo() != null ? d.getAvailableTo().toString() : "N/A",
                clean(d.getPassword(), "N/A"));
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
            if (parts.length > 2 && !"N/A".equals(parts[2])) d.setEmail(parts[2]);
            if (parts.length > 3 && !"N/A".equals(parts[3])) d.setPhone(parts[3]);
            if (parts.length > 4 && !"N/A".equals(parts[4])) d.setSpecialization(parts[4]);
            if (parts.length > 5 && !"N/A".equals(parts[5])) d.setLicenseNumber(parts[5]);
            if (parts.length > 6) {
                try {
                    d.setExperience(Integer.parseInt(parts[6]));
                } catch (NumberFormatException e) {
                    d.setExperience(0);
                }
            }
            if (parts.length > 7) {
                try {
                    d.setConsultationFee(Double.parseDouble(parts[7]));
                } catch (NumberFormatException e) {
                    d.setConsultationFee(0.0);
                }
            }
            if (parts.length > 8 && !"N/A".equals(parts[8])) d.setAvailableDays(parts[8]);
            if (parts.length > 9 && !"N/A".equals(parts[9])) {
                try {
                    d.setAvailableFrom(java.time.LocalTime.parse(parts[9]));
                } catch (Exception e) {
                    // ignore
                }
            }
            if (parts.length > 10 && !"N/A".equals(parts[10])) {
                try {
                    d.setAvailableTo(java.time.LocalTime.parse(parts[10]));
                } catch (Exception e) {
                    // ignore
                }
            }
            if (parts.length > 11 && !"N/A".equals(parts[11])) d.setPassword(parts[11]);
            return d;
        } catch (Exception e) {
            return null;
        }
    }
}


