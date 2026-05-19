package com.medicalapp.billingandpayment.dto;

import com.medicalapp.billingandpayment.entity.Billing;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BillingDTO {

    private Long id;

    private Long appointmentId;

    private Long patientId;
    private String billingCategory;

    @NotNull(message = "Amount is required")
    private Double amount;

    private Billing.PaymentType paymentType;

    private Billing.PaymentStatus status;

    private String invoiceNumber;

    private String paymentResult;

    private LocalDateTime paidAt;
}
