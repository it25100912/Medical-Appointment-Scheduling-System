package com.medicalapp.admin.repository;

import com.medicalapp.admin.entity.AuditLog;
import com.medicalapp.common.storage.FileStorageUtil;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class FileAuditRepository {
    private final FileStorageUtil<AuditLog> storage = new FileStorageUtil<>("audit.txt");

    public List<AuditLog> findAll() {
        return storage.readFromFile(this::mapToLog);
    }

    public void save(AuditLog log) {
        List<AuditLog> logs = findAll();
        log.setId(System.currentTimeMillis());
        log.setTimestamp(LocalDateTime.now());
        logs.add(log);
        storage.writeToFile(logs, this::mapToString);
    }

    private String mapToString(AuditLog l) {
        return String.format("%d,%s,%s,%s,%s,%s,%s",
                l.getId(),
                l.getUserId() != null ? l.getUserId() : "System",
                l.getAction() != null ? l.getAction() : "ACTION",
                l.getDescription() != null ? l.getDescription() : "N/A",
                l.getStatus() != null ? l.getStatus() : "INFO",
                l.getReference() != null ? l.getReference() : "N/A",
                l.getTimestamp() != null ? l.getTimestamp().toString() : LocalDateTime.now().toString());
    }

    private AuditLog mapToLog(String line) {
        String[] parts = line.split(",");
        if (parts.length < 2) return AuditLog.builder().build();

        try {
            return AuditLog.builder()
                    .id(Long.parseLong(parts[0]))
                    .userId(parts[1])
                    .action(parts.length > 2 ? parts[2] : "N/A")
                    .description(parts.length > 3 ? parts[3] : "N/A")
                    .status(parts.length > 4 ? parts[4] : "N/A")
                    .reference(parts.length > 5 ? parts[5] : "N/A")
                    .timestamp(parts.length > 6 && !"null".equals(parts[6]) ? LocalDateTime.parse(parts[6]) : LocalDateTime.now())
                    .build();
        } catch (Exception e) {
            System.err.println("Error parsing audit record: " + line + " - " + e.getMessage());
            return AuditLog.builder().build();
        }
    }
}

