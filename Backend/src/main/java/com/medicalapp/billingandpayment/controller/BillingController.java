package com.medicalapp.billingandpayment.controller;

import com.medicalapp.billingandpayment.dto.BillingDTO;
import com.medicalapp.billingandpayment.entity.Billing;
import com.medicalapp.billingandpayment.service.IBillingService;
import com.medicalapp.common.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BillingController {

    private final IBillingService billingService;

    @PostMapping
    public ResponseEntity<BillingDTO> createBill(@Valid @RequestBody BillingDTO dto) {
        return new ResponseEntity<>(billingService.createBill(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public List<BillingDTO> getAllBills() {
        // Patients can only view their own billing information
        if (SecurityUtils.isPatient()) {
            Long patientId = SecurityUtils.getCurrentUserId();
            return billingService.getBillsByPatient(patientId);
        }
        // Admins and doctors can see all bills
        return billingService.getAllBills();
    }

    @GetMapping("/{id}")
    public BillingDTO getBillById(@PathVariable Long id) {
        BillingDTO bill = billingService.getBillById(id);
        
        // Patients can only view their own billing information
        if (SecurityUtils.isPatient()) {
            Long currentPatientId = SecurityUtils.getCurrentUserId();
            if (!bill.getPatientId().equals(currentPatientId)) {
                throw new RuntimeException("Unauthorized: You can only access your own billing information");
            }
        }
        return bill;
    }

    @GetMapping("/patient/{patientId}")
    public List<BillingDTO> getBillsByPatient(@PathVariable Long patientId) {
        // Patients can only view their own billing information
        if (SecurityUtils.isPatient()) {
            Long currentPatientId = SecurityUtils.getCurrentUserId();
            if (!patientId.equals(currentPatientId)) {
                throw new RuntimeException("Unauthorized: You can only access your own billing information");
            }
        }
        return billingService.getBillsByPatient(patientId);
    }

    @GetMapping("/invoice/{invoiceNumber}")
    public BillingDTO getBillByInvoice(@PathVariable String invoiceNumber) {
        return billingService.getBillByInvoiceNumber(invoiceNumber);
    }

    @PutMapping("/{id}/pay")
    public ResponseEntity<String> processPayment(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Billing.PaymentType type = Billing.PaymentType.valueOf((String) body.get("type"));
        @SuppressWarnings("unchecked")
        Map<String, String> details = (Map<String, String>) body.get("details");
        
        String result = billingService.processPayment(id, type, details);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBill(@PathVariable Long id) {
        billingService.deleteBill(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public BillingDTO updateBill(@PathVariable Long id, @RequestBody BillingDTO dto) {
        return billingService.updateBill(id, dto);
    }
}
