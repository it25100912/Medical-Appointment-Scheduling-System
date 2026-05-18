package com.medicalapp.auth.entity;

import jakarta.persistence.*;

public class User {

    @Column(unique = true)
    private String email;
    private String password;
    private String name;
    private String nic;
    private String phone;
    private String licenseNumber;

}
