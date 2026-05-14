package com.medicalapp.medicalrecord.repository;

import com.medicalapp.common.storage.FileStorageUtil;
import com.medicalapp.medicalrecord.entity.MedicalRecord;
import com.medicalapp.medicalrecord.entity.PrescriptionRecord;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class FileMedicalRecordRepository {
    private final FileStorageUtil<MedicalRecord> storage = new FileStorageUtil<>("medical_records.txt");

    public List<MedicalRecord> findAll() {
        return storage.readFromFile(this::mapToRecord);
    }

    public Optional<MedicalRecord> findById(Long id) {
        return findAll().stream().filter(r -> r.getId().equals(id)).findFirst();
    }

    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }

    public MedicalRecord save(MedicalRecord record) {
        List<MedicalRecord> records = findAll();
        if (record.getId() == null) {
            record.setId(System.currentTimeMillis());
            records.add(record);
        } else {
            records = records.stream()
                    .map(r -> r.getId().equals(record.getId()) ? record : r)
                    .collect(Collectors.toList());
        }
        storage.writeToFile(records, this::mapToString);
        return record;
    }

    public void deleteById(Long id) {
        List<MedicalRecord> records = findAll().stream()
                .filter(r -> !r.getId().equals(id))
                .collect(Collectors.toList());
        storage.writeToFile(records, this::mapToString);
    }

    private String mapToString(MedicalRecord r) {
        return String.format("%d,%s,%s,%s,%s,%s",
                r.getId(),
                r.getPatientId() != null ? r.getPatientId().toString() : "0",
                r.getDoctorId() != null ? r.getDoctorId().toString() : "0",
                clean(r.getDiagnosis(), "N/A"),
                clean(r.getPrescription(), "N/A"),
                clean(r.getNotes(), "N/A"));
    }

    private String clean(String input, String def) {
        if (input == null || input.trim().isEmpty()) return def;
        return input.replace(",", " ").replace("\n", " ").replace("\r", " ").trim();
    }

    private MedicalRecord mapToRecord(String line) {
        String[] parts = line.split(",");
        MedicalRecord r = new PrescriptionRecord();
        if (parts.length < 2) return null;

        try {
            r.setId(Long.parseLong(parts[0]));
            if (parts.length > 1 && !"N/A".equals(parts[1])) r.setPatientId(Long.parseLong(parts[1]));
            if (parts.length > 2 && !"N/A".equals(parts[2])) r.setDoctorId(Long.parseLong(parts[2]));
            if (parts.length > 3) r.setDiagnosis(parts[3]);
            if (parts.length > 4) r.setPrescription(parts[4]);
            if (parts.length > 5) r.setNotes(parts[5]);
            return r;
        } catch (Exception e) {
            return null;
        }
    }
}


