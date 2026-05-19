package com.medicalapp.admin.controller;

import com.medicalapp.admin.entity.AuditLog;
import com.medicalapp.admin.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


  //Controller class for handling Audit Log related APIs

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    // Repository layer object injection
    private final AuditLogRepository auditLogRepository;


     // Get all audit logs

    @GetMapping
    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAll();
    }
}