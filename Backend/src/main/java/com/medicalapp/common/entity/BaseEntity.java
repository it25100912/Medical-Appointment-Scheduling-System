package com.medicalapp.common.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * BaseEntity.java
 *
 * Abstract base class for all entity models to minimize code duplication.
 * Provides mandatory auditing fields for every table.
 *
 * OOP Concepts Applied:
 * - INHERITANCE: Used as a @MappedSuperclass to provide common fields to child entities.
 * - ENCAPSULATION: Private fields accessed only through Lombok-generated getters/setters.
 *
 * Component: Common
 * Layer: Entity (MappedSuperclass)
 */
@Data
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
