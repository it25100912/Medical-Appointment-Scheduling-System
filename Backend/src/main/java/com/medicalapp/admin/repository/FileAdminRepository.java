package com.medicalapp.admin.repository;

import com.medicalapp.admin.entity.Admin;
import com.medicalapp.common.storage.FileStorageUtil;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class FileAdminRepository {
    private final FileStorageUtil<Admin> storage = new FileStorageUtil<>("admins.txt");

    public List<Admin> findAll() {
        return storage.readFromFile(this::mapToAdmin);
    }

    public Optional<Admin> findById(Long id) {
        return findAll().stream().filter(a -> a.getId().equals(id)).findFirst();
    }

    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }

    public Admin save(Admin admin) {
        List<Admin> admins = findAll();
        if (admin.getId() == null) {
            admin.setId(System.currentTimeMillis());
            admins.add(admin);
        } else {
            admins = admins.stream()
                    .map(a -> a.getId().equals(admin.getId()) ? admin : a)
                    .collect(Collectors.toList());
        }
        storage.writeToFile(admins, this::mapToString);
        return admin;
    }

    public void deleteById(Long id) {
        List<Admin> admins = findAll().stream()
                .filter(a -> !a.getId().equals(id))
                .collect(Collectors.toList());
        storage.writeToFile(admins, this::mapToString);
    }

    private String mapToString(Admin a) {
        return String.format("%d,%s,%s,%s,%s",
                a.getId(),
                clean(a.getUsername(), "Unknown"),
                clean(a.getEmail(), "N/A"),
                a.getRole() != null ? a.getRole().toString() : "ADMIN",
                clean(a.getPassword(), "pass123"));
    }

    private String clean(String input, String def) {
        if (input == null || input.trim().isEmpty()) return def;
        return input.replace(",", " ").replace("\n", " ").replace("\r", " ").trim();
    }

    private Admin mapToAdmin(String line) {
        String[] parts = line.split(",");
        Admin a = new Admin();
        if (parts.length < 2) return null;

        try {
            a.setId(Long.parseLong(parts[0]));
            a.setUsername(parts[1]);
            if (parts.length > 2 && !"N/A".equals(parts[2])) a.setEmail(parts[2]);
            if (parts.length > 3 && !"N/A".equals(parts[3])) {
                try {
                    a.setRole(Admin.AdminRole.valueOf(parts[3]));
                } catch (Exception e) {
                    a.setRole(Admin.AdminRole.ADMIN);
                }
            }
            if (parts.length > 4) a.setPassword(parts[4]);
            return a;
        } catch (Exception e) {
            return null;
        }
    }
}


