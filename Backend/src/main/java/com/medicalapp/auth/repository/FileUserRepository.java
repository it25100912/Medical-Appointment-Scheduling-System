package com.medicalapp.auth.repository;

import com.medicalapp.auth.entity.User;
import com.medicalapp.common.storage.FileStorageUtil;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class FileUserRepository {
    private final FileStorageUtil<User> storage = new FileStorageUtil<>("users.txt");

    public List<User> findAll() {
        return storage.readFromFile(this::mapToUser);
    }

    public Optional<User> findById(Long id) {
        return findAll().stream().filter(u -> u.getId().equals(id)).findFirst();
    }

    public Optional<User> findByEmail(String email) {
        return findAll().stream().filter(u -> u.getEmail() != null && u.getEmail().equals(email)).findFirst();
    }

    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }

    public User save(User user) {
        List<User> users = findAll();
        if (user.getId() == null) {
            user.setId(System.currentTimeMillis());
            users.add(user);
        } else {
            boolean updated = false;
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getId().equals(user.getId())) {
                    users.set(i, user);
                    updated = true;
                    break;
                }
            }
            if (!updated) {
                users.add(user);
            }
        }
        storage.writeToFile(users, this::mapToString);
        return user;
    }

    public void deleteById(Long id) {
        List<User> users = findAll().stream()
                .filter(u -> !u.getId().equals(id))
                .collect(Collectors.toList());
        storage.writeToFile(users, this::mapToString);
    }

    private String mapToString(User u) {
        return String.format("%d,%s,%s,%s,%s,%s,%s,%s",
                u.getId(),
                clean(u.getEmail(), "N/A"),
                clean(u.getPassword(), "N/A"),
                clean(u.getName(), "Unknown"),
                clean(u.getNic(), "N/A"),
                clean(u.getPhone(), "N/A"),
                clean(u.getLicenseNumber(), "N/A"),
                u.getRole() != null ? u.getRole().name() : "PATIENT");
    }

    private String clean(String input, String def) {
        if (input == null || input.trim().isEmpty()) return def;
        return input.replace(",", " ").replace("\n", " ").replace("\r", " ").trim();
    }

    private User mapToUser(String line) {
        String[] parts = line.split(",");
        User u = new User();
        if (parts.length < 2) return null;

        try {
            u.setId(Long.parseLong(parts[0]));
            if (parts.length > 1 && !"N/A".equals(parts[1])) u.setEmail(parts[1]);
            if (parts.length > 2 && !"N/A".equals(parts[2])) u.setPassword(parts[2]);
            if (parts.length > 3 && !"Unknown".equals(parts[3])) u.setName(parts[3]);
            if (parts.length > 4 && !"N/A".equals(parts[4])) u.setNic(parts[4]);
            if (parts.length > 5 && !"N/A".equals(parts[5])) u.setPhone(parts[5]);
            if (parts.length > 6 && !"N/A".equals(parts[6])) u.setLicenseNumber(parts[6]);
            if (parts.length > 7 && !"PATIENT".equals(parts[7])) {
                try {
                    u.setRole(User.Role.valueOf(parts[7]));
                } catch (IllegalArgumentException e) {
                    u.setRole(User.Role.PATIENT);
                }
            } else {
                u.setRole(User.Role.PATIENT);
            }
            return u;
        } catch (Exception e) {
            return null;
        }
    }
}
