package com.medicalapp.billingandpayment.entity;

import com.medicalapp.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "payments")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "payment_method")
@EqualsAndHashCode(callSuper = true)
public abstract class Payment extends BaseEntity {

    private Double amount;
    private LocalDateTime paidAt;

    public abstract String processPayment();
}
