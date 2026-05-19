package com.medicalapp.billingandpayment.entity;

import com.medicalapp.appointment.entity.Appointment;
import com.medicalapp.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "billings")
@EqualsAndHashCode(callSuper = true)
public class Billing extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @NotNull(message = "Billing amount is required")
    private Double amount;

    private Long patientId;
    private String billingCategory;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(unique = true)
    private String invoiceNumber;

    @Column(columnDefinition = "TEXT")
    private String paymentResult;

    private LocalDateTime paidAt;

    public enum PaymentType {
        ONLINE, CASH
    }

    public enum PaymentStatus {
        PENDING, PAID, FAILED, REFUNDED
    }
}
