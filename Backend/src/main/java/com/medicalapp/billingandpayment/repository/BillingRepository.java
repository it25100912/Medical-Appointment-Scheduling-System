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

    List<Billing> findByPatientId(Long patientId);

    List<Billing> findByAppointment_Patient_Id(Long patientId);
}
