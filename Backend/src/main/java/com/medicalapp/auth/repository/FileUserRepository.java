package com.medicalapp.auth.repository;

import com.medicalapp.auth.entity.User;
import com.medicalapp.common.storage.FileStorageUtil;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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


}
