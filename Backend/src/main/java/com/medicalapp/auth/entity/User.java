package com.medicalapp.auth.entity;

import com.medicalapp.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class User {

    @Column(unique = true)
    private String email;
    private String password;
    private String name;
    private String nic;
    private String phone;
    private String licenseNumber;

    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role {
        ADMIN, DOCTOR, PATIENT
    }

    

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
