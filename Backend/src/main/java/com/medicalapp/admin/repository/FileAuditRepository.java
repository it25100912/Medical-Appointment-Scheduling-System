package com.medicalapp.admin.repository;

import com.medicalapp.admin.entity.AuditLog;
import com.medicalapp.common.storage.FileStorageUtil;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


 // Repository class for handling Audit Log file storage operations

@Repository
public class FileAuditRepository {

    // File storage utility object for audit logs
    private final FileStorageUtil<AuditLog> storage = new FileStorageUtil<>("audit.txt");


     // Retrieve all audit logs from file

    public List<AuditLog> findAll() {
        return storage.readFromFile(this::mapToLog);
    }

     //  Save a new audit log

    public void save(AuditLog log) {

        List<AuditLog> logs = findAll();

        // Generate unique ID using current timestamp
        log.setId(System.currentTimeMillis());

        // Set current timestamp
        log.setTimestamp(LocalDateTime.now());

        // Add log to list
        logs.add(log);

        // Save updated logs to file
        storage.writeToFile(logs, this::mapToString);
    }


     // Convert AuditLog object into String format for file storage

    private String mapToString(AuditLog l) {

        return String.format("%d,%s,%s,%s,%s,%s,%s",
                l.getId(),
                l.getUserId() != null ? l.getUserId() : "System",
                l.getAction() != null ? l.getAction() : "ACTION",
                l.getDescription() != null ? l.getDescription() : "N/A",
                l.getStatus() != null ? l.getStatus() : "INFO",
                l.getReference() != null ? l.getReference() : "N/A",
                l.getTimestamp() != null
                        ? l.getTimestamp().toString()
                        : LocalDateTime.now().toString());
    }


     // Convert file line into AuditLog object

    private AuditLog mapToLog(String line) {

        String[] parts = line.split(",");

        // Return empty object if line is invalid
        if (parts.length < 2) {
            return AuditLog.builder().build();
        }

        try {

            return AuditLog.builder()

                    // Set log ID
                    .id(Long.parseLong(parts[0]))

                    // Set user ID
                    .userId(parts[1])

                    // Set action
                    .action(parts.length > 2 ? parts[2] : "N/A")

                    // Set description
                    .description(parts.length > 3 ? parts[3] : "N/A")

                    // Set status
                    .status(parts.length > 4 ? parts[4] : "N/A")

                    // Set reference
                    .reference(parts.length > 5 ? parts[5] : "N/A")

                    // Set timestamp
                    .timestamp(parts.length > 6 && !"null".equals(parts[6])
                            ? LocalDateTime.parse(parts[6])
                            : LocalDateTime.now())

                    .build();

        } catch (Exception e) {

            // Print error if parsing fails
            System.err.println("Error parsing audit record: "
                    + line + " - " + e.getMessage());

            return AuditLog.builder().build();
        }
    }
}