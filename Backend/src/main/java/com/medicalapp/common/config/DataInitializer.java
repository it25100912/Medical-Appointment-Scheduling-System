package com.medicalapp.common.config;

import com.medicalapp.admin.entity.Admin;
import com.medicalapp.admin.repository.AdminRepository;
import com.medicalapp.auth.entity.User;
import com.medicalapp.auth.repository.FileUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final FileUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminRepository adminRepository;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            if (!userRepository.existsByEmail("admin@gmail.com")) {
                User admin = User.builder()
                        .email("admin@gmail.com")
                        .password(passwordEncoder.encode("admin123"))
                        .name("System Admin")
                        .role(User.Role.ADMIN)
                        .build();
                userRepository.save(admin);
                System.out.println("Default Admin User created: admin@gmail.com / admin123");
            }

            if (!userRepository.existsByEmail("doctor@gmail.com")) {
                User doctor = User.builder()
                        .email("doctor@gmail.com")
                        .password(passwordEncoder.encode("doctor123"))
                        .name("Dr. Smith")
                        .role(User.Role.DOCTOR)
                        .build();
                userRepository.save(doctor);
                System.out.println("Default Doctor User created: doctor@gmail.com / doctor123");
            }

            if (!userRepository.existsByEmail("patient@gmail.com")) {
                User patient = User.builder()
                        .email("patient@gmail.com")
                        .password(passwordEncoder.encode("patient123"))
                        .name("John Doe")
                        .role(User.Role.PATIENT)
                        .build();
                userRepository.save(patient);
                System.out.println("Default Patient User created: patient@gmail.com / patient123");
            }

            // Sync users.txt ADMINs with H2 database
            for (User u : userRepository.findAll()) {
                if (u.getRole() == User.Role.ADMIN) {
                    if (!adminRepository.existsById(u.getId())) {
                        Admin adm = new Admin();
                        adm.setId(u.getId());
                        adm.setUsername(u.getName());
                        adm.setEmail(u.getEmail());
                        adm.setPassword(u.getPassword());
                        adm.setRole(Admin.AdminRole.ADMIN);
                        adminRepository.save(adm);
                    }
                }
            }
        };
    }
}
