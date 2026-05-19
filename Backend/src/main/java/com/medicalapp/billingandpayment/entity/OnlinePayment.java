package com.medicalapp.billingandpayment.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@DiscriminatorValue("ONLINE")
@EqualsAndHashCode(callSuper = true)
public class OnlinePayment extends Payment {

    private String transactionId;
    private String paymentGateway;

    @Override
    public String processPayment() {
        this.setTransactionId(UUID.randomUUID().toString());
        this.setPaidAt(LocalDateTime.now());
        return String.format("Online payment of LKR %.2f processed via %s. Transaction ID: %s",
                getAmount(), paymentGateway, transactionId);
    }
}
