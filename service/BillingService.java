package com.medicalapp.billingandpayment.service;

import com.medicalapp.billingandpayment.dto.BillingDTO;
import com.medicalapp.billingandpayment.entity.*;
import com.medicalapp.appointment.entity.Appointment;
import com.medicalapp.appointment.repository.FileAppointmentRepository;
import com.medicalapp.billingandpayment.repository.FileBillingRepository;
import com.medicalapp.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BillingService implements IBillingService {

    private final FileBillingRepository billingRepository;
    private final FileAppointmentRepository appointmentRepository;

    @Override
    public BillingDTO createBill(BillingDTO dto) {
        Billing billing = new Billing();
        billing.setAmount(dto.getAmount() != null ? dto.getAmount() : 0.0);
        billing.setPatientId(dto.getPatientId());
        billing.setStatus(dto.getStatus() != null ? dto.getStatus() : Billing.PaymentStatus.PENDING);
        billing.setInvoiceNumber("INV-" + System.currentTimeMillis());
        billing.setBillingCategory(dto.getBillingCategory() != null ? dto.getBillingCategory() : "General");

        return mapEntityToDto(billingRepository.save(billing));
    }

    @Override
    public List<BillingDTO> getAllBills() {
        return billingRepository.findAll().stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public BillingDTO getBillById(Long id) {
        Billing billing = billingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found"));
        return mapEntityToDto(billing);
    }

    @Override
    public List<BillingDTO> getBillsByPatient(Long patientId) {
        return billingRepository.findAll().stream()
                .filter(b -> b.getPatientId().equals(patientId))
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public BillingDTO getBillByInvoiceNumber(String invoiceNumber) {
        Billing billing = billingRepository.findAll().stream()
                .filter(b -> b.getInvoiceNumber().equals(invoiceNumber))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found with invoice: " + invoiceNumber));
        return mapEntityToDto(billing);
    }

    @Override
    public String processPayment(Long id, Billing.PaymentType type, Map<String, String> details) {
        Billing billing = billingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found"));

        billing.setStatus(Billing.PaymentStatus.PAID);
        billingRepository.save(billing);
        return "Payment processed via " + type;
    }

    @Override
    public void deleteBill(Long id) {
        billingRepository.deleteById(id);
    }

    @Override
    public BillingDTO updateBill(Long id, BillingDTO dto) {
        Billing billing = billingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found"));
        
        if (dto.getAmount() != null) billing.setAmount(dto.getAmount());
        if (dto.getPatientId() != null) billing.setPatientId(dto.getPatientId());
        if (dto.getBillingCategory() != null) billing.setBillingCategory(dto.getBillingCategory());
        if (dto.getStatus() != null) billing.setStatus(dto.getStatus());

        return mapEntityToDto(billingRepository.save(billing));
    }

    private BillingDTO mapEntityToDto(Billing b) {
        BillingDTO dto = new BillingDTO();
        dto.setId(b.getId());
        dto.setPatientId(b.getPatientId());
        dto.setAmount(b.getAmount());
        dto.setStatus(b.getStatus());
        dto.setInvoiceNumber(b.getInvoiceNumber());
        dto.setBillingCategory(b.getBillingCategory());
        return dto;
    }
}
