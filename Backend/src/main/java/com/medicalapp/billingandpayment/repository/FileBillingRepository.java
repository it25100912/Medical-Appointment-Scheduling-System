package com.medicalapp.billingandpayment.repository;

import com.medicalapp.billingandpayment.entity.Billing;
import com.medicalapp.appointment.entity.Appointment;
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

    public List<Billing> findByPatientId(Long patientId) {
        return findAll().stream()
                .filter(b -> b.getPatientId() != null && b.getPatientId().equals(patientId))
                .collect(Collectors.toList());
    }

    public Optional<Billing> findByInvoiceNumber(String invoiceNumber) {
        return findAll().stream()
                .filter(b -> b.getInvoiceNumber() != null && b.getInvoiceNumber().equals(invoiceNumber))
                .findFirst();
    }

    private String mapToString(Billing b) {
        return String.format("%d,%s,%s,%s,%s,%s,%s",
                b.getId(),
                b.getPatientId() != null ? b.getPatientId().toString() : "0",
                clean(b.getInvoiceNumber(), "N/A"),
                clean(b.getBillingCategory(), "General"),
                b.getAmount() != null ? b.getAmount().toString() : "0.0",
                b.getStatus() != null ? b.getStatus().toString() : "PENDING",
                b.getAppointment() != null ? b.getAppointment().getId().toString() : "0");
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
            if (parts.length > 6 && !"0".equals(parts[6])) {
                Appointment a = new Appointment();
                a.setId(Long.parseLong(parts[6]));
                b.setAppointment(a);
            }
            return b;
        } catch (Exception e) {
            return null;
        }
    }
}


