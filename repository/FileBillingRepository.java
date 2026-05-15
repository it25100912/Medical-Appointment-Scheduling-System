package com.medicalapp.billingandpayment.dto;

import com.medicalapp.billingandpayment.entity.Billing;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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



package com.medicalapp.billingandpayment.repository;

import com.medicalapp.billingandpayment.entity.Billing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillingRepository extends JpaRepository<Billing, Long> {

    Optional<Billing> findByAppointment_Id(Long appointmentId);

    Optional<Billing> findByInvoiceNumber(String invoiceNumber);

    List<Billing> findByStatus(Billing.PaymentStatus status);

    List<Billing> findByAppointment_Patient_Id(Long patientId);
}




package com.medicalapp.billingandpayment.repository;

import com.medicalapp.billingandpayment.entity.Billing;
import com.medicalapp.common.storage.FileStorageUtil;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class FileBillingRepository {
    private final FileStorageUtil<Billing> storage = new FileStorageUtil<>("billing.txt");

    public List<Billing> findAll() {
        return storage.readFromFile(this::mapToBilling);
    }

    public Optional<Billing> findById(Long id) {
        return findAll().stream().filter(b -> b.getId().equals(id)).findFirst();
    }

    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }

    public Billing save(Billing billing) {
        List<Billing> bills = findAll();
        if (billing.getId() == null) {
            billing.setId(System.currentTimeMillis());
            bills.add(billing);
        } else {
            bills = bills.stream()
                    .map(b -> b.getId().equals(billing.getId()) ? billing : b)
                    .collect(Collectors.toList());
        }
        storage.writeToFile(bills, this::mapToString);
        return billing;
    }

    public void deleteById(Long id) {
        List<Billing> bills = findAll().stream()
                .filter(b -> !b.getId().equals(id))
                .collect(Collectors.toList());
        storage.writeToFile(bills, this::mapToString);
    }

    private String mapToString(Billing b) {
        return String.format("%d,%s,%s,%s,%s,%s",
                b.getId(),
                b.getPatientId() != null ? b.getPatientId().toString() : "0",
                clean(b.getInvoiceNumber(), "N/A"),
                clean(b.getBillingCategory(), "General"),
                b.getAmount() != null ? b.getAmount().toString() : "0.0",
                b.getStatus() != null ? b.getStatus().toString() : "PENDING");
    }

    private String clean(String input, String def) {
        if (input == null || input.trim().isEmpty()) return def;
        return input.replace(",", " ").replace("\n", " ").replace("\r", " ").trim();
    }

    private Billing mapToBilling(String line) {
        String[] parts = line.split(",");
        Billing b = new Billing();
        if (parts.length < 2) return null;

        try {
            b.setId(Long.parseLong(parts[0]));
            if (parts.length > 1 && !"0".equals(parts[1])) b.setPatientId(Long.parseLong(parts[1]));
            if (parts.length > 2) b.setInvoiceNumber(parts[2]);
            if (parts.length > 3) b.setBillingCategory(parts[3]);
            if (parts.length > 4) b.setAmount(Double.parseDouble(parts[4]));
            if (parts.length > 5) b.setStatus(Billing.PaymentStatus.valueOf(parts[5]));
            return b;
        } catch (Exception e) {
            return null;
        }
    }
}








