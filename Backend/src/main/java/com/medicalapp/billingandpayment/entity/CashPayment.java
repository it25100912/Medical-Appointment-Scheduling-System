package com.medicalapp.billingandpayment.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@Entity
@DiscriminatorValue("CASH")
@EqualsAndHashCode(callSuper = true)
public class CashPayment extends Payment {

    private String receivedBy;
    private Double cashReceived;
    private Double changeGiven;

    @Override
    public String processPayment() {
        this.setChangeGiven(cashReceived - getAmount());
        this.setPaidAt(LocalDateTime.now());
        return String.format("Cash payment of LKR %.2f received by %s. Cash received: %.2f, Change: %.2f",
                getAmount(), receivedBy, cashReceived, changeGiven);
    }
}
