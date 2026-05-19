package com.medicalapp.common.config;

import com.medicalapp.admin.entity.Admin;
import com.medicalapp.admin.entity.AuditLog;
import com.medicalapp.admin.repository.AuditLogRepository;
import com.medicalapp.admin.service.IAdminService;
import com.medicalapp.patient.dto.PatientDTO;
import com.medicalapp.patient.entity.Patient;
import com.medicalapp.patient.service.IPatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final IPatientService patientService;

    private final IAdminService adminService;
    private final AuditLogRepository auditLogRepository;

    @Override
    public void run(String... args) {
        System.out.println("=== INITIALIZING SAMPLE DATA ===");
        System.out.println("=== SAMPLE DATA SEEDING COMPLETED ===");
    }
}
