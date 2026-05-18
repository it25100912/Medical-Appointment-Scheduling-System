package com.medicalapp.medicalrecord.dto;

import com.medicalapp.medicalrecord.entity.MedicalRecord;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class MedicalRecordDTO {

    private Long id;

     // Patient ID is required for creating a medical record
    @NotNull(message = "Patient ID is required")
    private Long patientId;

     // Doctor ID is required for creating a medical record
    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    
    @NotNull(message = "Record date is required")
    private LocalDate recordDate;

    private String diagnosis;


    private MedicalRecord.RecordType recordType;

    private String medicineName;
    private String dosage;
    private String duration;

    private String testName;
    private String testResult;
    private String referenceRange;

    private String summary;
}
