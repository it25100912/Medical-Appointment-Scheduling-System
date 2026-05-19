package com.medicalapp.billingandpayment.service;

import com.medicalapp.billingandpayment.dto.BillingDTO;
import com.medicalapp.billingandpayment.entity.Billing.PaymentType;
import java.util.List;
import java.util.Map;

public interface IBillingService {

    BillingDTO createBill(BillingDTO dto);

    List<BillingDTO> getAllBills();

    BillingDTO getBillById(Long id);

    List<BillingDTO> getBillsByPatient(Long patientId);

    BillingDTO getBillByInvoiceNumber(String invoiceNumber);

    String processPayment(Long id, PaymentType type, Map<String, String> paymentDetails);

    void deleteBill(Long id);

    BillingDTO updateBill(Long id, BillingDTO dto);
}
