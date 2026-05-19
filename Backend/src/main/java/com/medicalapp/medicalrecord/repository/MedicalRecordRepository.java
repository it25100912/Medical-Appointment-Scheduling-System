package com.medicalapp.medicalrecord.repository;

import com.medicalapp.medicalrecord.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    List<MedicalRecord> findByPatient_Id(Long patientId);

    List<MedicalRecord> findByDoctor_Id(Long doctorId);

    default List<MedicalRecord> findByPatientId(Long patientId) {
        return findByPatient_Id(patientId);
    }

    default List<MedicalRecord> findByDoctorId(Long doctorId) {
        return findByDoctor_Id(doctorId);
    }

    List<MedicalRecord> findByRecordType(MedicalRecord.RecordType type);

    List<MedicalRecord> findByRecordDateBetween(LocalDate from, LocalDate to);
}
