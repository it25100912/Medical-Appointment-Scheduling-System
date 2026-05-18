package com.medicalapp.patient.repository;

import com.medicalapp.common.storage.FileStorageUtil;
import com.medicalapp.patient.entity.Patient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class FilePatientRepository {
    private final FileStorageUtil<Patient> storage = new FileStorageUtil<>("patients.txt");

    public List<Patient> findAll() {
        return storage.readFromFile(this::mapToPatient);
    }

    public Optional<Patient> findById(Long id) {
        return findAll().stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }

    public Patient save(Patient patient) {
        List<Patient> patients = findAll();
        if (patient.getId() == null) {
            patient.setId(System.currentTimeMillis());
            patients.add(patient);
        } else {
            boolean updated = false;
            for (int i = 0; i < patients.size(); i++) {
                if (patients.get(i).getId().equals(patient.getId())) {
                    patients.set(i, patient);
                    updated = true;
                    break;
                }
            }
            if (!updated) {
                patients.add(patient);
            }
        }
        storage.writeToFile(patients, this::mapToString);
        return patient;
    }

    public void deleteById(Long id) {
        List<Patient> patients = findAll().stream()
                .filter(p -> !p.getId().equals(id))
                .collect(Collectors.toList());
        storage.writeToFile(patients, this::mapToString);
    }

    public Optional<Patient> findByNic(String nic) {
        return findAll().stream().filter(p -> p.getNic() != null && p.getNic().equals(nic)).findFirst();
    }

    public Optional<Patient> findByPhone(String phone) {
        return findAll().stream().filter(p -> p.getPhone() != null && p.getPhone().equals(phone)).findFirst();
    }

    public Optional<Patient> findByEmail(String email) {
        return findAll().stream().filter(p -> p.getEmail() != null && p.getEmail().equals(email)).findFirst();
    }

    // Comma Separated Format for Notepad
    private String mapToString(Patient p) {
        return String.format("%d,%s,%s,%s,%s,%s,%s,%s,%s",
                p.getId(),
                clean(p.getName(), "Unknown"),
                clean(p.getEmail(), "N/A"),
                clean(p.getPhone(), "N/A"),
                clean(p.getNic(), "N/A"),
                clean(p.getAddress(), "N/A"),
                clean(p.getBloodGroup(), "N/A"),
                p.getDateOfBirth() != null ? p.getDateOfBirth().toString() : "N/A",
                clean(p.getPassword(), "N/A"));
    }

    private String clean(String input, String def) {
        if (input == null || input.trim().isEmpty()) return def;
        return input.replace(",", " ").replace("\n", " ").replace("\r", " ").trim();
    }

    private Patient mapToPatient(String line) {
        String[] parts = line.split(",");
        Patient p = new Patient();
        if (parts.length < 2) return null;

        try {
            p.setId(Long.parseLong(parts[0]));
            p.setName(parts[1]);
            if (parts.length > 2 && !"N/A".equals(parts[2])) p.setEmail(parts[2]);
            if (parts.length > 3 && !"N/A".equals(parts[3])) p.setPhone(parts[3]);
            if (parts.length > 4 && !"N/A".equals(parts[4])) p.setNic(parts[4]);
            if (parts.length > 5 && !"N/A".equals(parts[5])) p.setAddress(parts[5]);
            if (parts.length > 6 && !"N/A".equals(parts[6])) p.setBloodGroup(parts[6]);
            if (parts.length > 7 && !"N/A".equals(parts[7])) p.setDateOfBirth(parts[7]);
            if (parts.length > 8 && !"N/A".equals(parts[8])) p.setPassword(parts[8]);
            return p;
        } catch (Exception e) {
            return null;
        }
    }
}


