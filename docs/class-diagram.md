# Medical Appointment Scheduling System - Class Diagram

This diagram summarizes the main backend classes in `Backend/src/main/java/com/medicalapp`.
It focuses on the domain model and Spring layer dependencies used by the application.

```mermaid
classDiagram
direction LR

class BaseEntity {
  <<abstract>>
  -Long id
  -LocalDateTime createdAt
  -LocalDateTime updatedAt
}

class AuthUser {
  -String email
  -String password
  -String name
  -String nic
  -String phone
  -String licenseNumber
  -Role role
  +getAuthorities()
  +getUsername()
}

class Admin {
  -String username
  -String email
  -String password
  -AdminRole role
}

class Patient {
  -String name
  -String nic
  -String phone
  -String email
  -String address
  -String bloodGroup
  -String dateOfBirth
  -String password
  -String medicalHistory
  -PatientType patientType
}

class InPatient
class OutPatient

class Doctor {
  <<abstract>>
  -String name
  -String email
  -String phone
  -String specialization
  -DoctorType doctorType
  -Double consultationFee
  -String licenseNumber
  -Integer experience
  -String availableDays
  -LocalTime availableFrom
  -LocalTime availableTo
  -String password
  +calculateConsultationFee() double
}

class GeneralDoctor {
  +calculateConsultationFee() double
}

class SpecialistDoctor {
  -String specialistArea
  +calculateConsultationFee() double
}

class Appointment {
  -LocalDate appointmentDate
  -LocalTime appointmentTime
  -Long patientId
  -Long doctorId
  -String reason
  -AppointmentStatus status
  -AppointmentType type
  -String notes
  +getPatientId() Long
  +getDoctorId() Long
}

class MedicalRecord {
  <<abstract>>
  -LocalDate recordDate
  -String diagnosis
  -Long patientId
  -Long doctorId
  -String prescription
  -String notes
  -RecordType recordType
  +getRecordSummary() String
}

class PrescriptionRecord {
  -String medicineName
  -String dosage
  -String duration
  +getRecordSummary() String
}

class LabRecord {
  -String testName
  -String testResult
  -String referenceRange
  +getRecordSummary() String
}

class Billing {
  -Double amount
  -Long patientId
  -String billingCategory
  -PaymentType paymentType
  -PaymentStatus status
  -String invoiceNumber
  -String paymentResult
  -LocalDateTime paidAt
}

class Payment {
  <<abstract>>
  -Double amount
  -LocalDateTime paidAt
  +processPayment() String
}

class OnlinePayment {
  -String transactionId
  -String paymentGateway
  +processPayment() String
}

class CashPayment {
  -String receivedBy
  -Double cashReceived
  -Double changeGiven
  +processPayment() String
}

class AuditLog {
  -Long id
  -String userId
  -String action
  -String description
  -String status
  -String reference
  -LocalDateTime timestamp
}

BaseEntity <|-- AuthUser
BaseEntity <|-- Admin
BaseEntity <|-- Patient
Patient <|-- InPatient
Patient <|-- OutPatient
BaseEntity <|-- Doctor
Doctor <|-- GeneralDoctor
Doctor <|-- SpecialistDoctor
BaseEntity <|-- Appointment
BaseEntity <|-- MedicalRecord
MedicalRecord <|-- PrescriptionRecord
MedicalRecord <|-- LabRecord
BaseEntity <|-- Billing
BaseEntity <|-- Payment
Payment <|-- OnlinePayment
Payment <|-- CashPayment

Patient "1" <-- "0..*" Appointment : patient
Doctor "1" <-- "0..*" Appointment : doctor
Patient "1" <-- "0..*" MedicalRecord : patient
Doctor "1" <-- "0..*" MedicalRecord : doctor
Appointment "1" <-- "0..1" Billing : appointment
```

## Backend Layer Diagram

```mermaid
classDiagram
direction TB

class PatientController
class DoctorController
class AppointmentController
class MedicalRecordController
class BillingController
class AdminController
class AuthController
class PatientDashboardController

class IPatientService {
  <<interface>>
}
class IDoctorService {
  <<interface>>
}
class IAppointmentService {
  <<interface>>
}
class IMedicalRecordService {
  <<interface>>
}
class IBillingService {
  <<interface>>
}
class IAdminService {
  <<interface>>
}

class PatientService
class DoctorService
class MedicalRecordService
class BillingService
class AdminService

class FileStorageUtil~T~ {
  -String filePath
  +writeToFile(List~T~ data, Function mapper)
  +readFromFile(Function mapper) List~T~
}

class FilePatientRepository
class FileDoctorRepository
class FileAppointmentRepository
class FileMedicalRecordRepository
class FileBillingRepository
class FileAdminRepository
class FileUserRepository
class FileAuditRepository

class PatientRepository {
  <<JpaRepository>>
}
class DoctorRepository {
  <<JpaRepository>>
}
class AppointmentRepository {
  <<JpaRepository>>
}
class MedicalRecordRepository {
  <<JpaRepository>>
}
class BillingRepository {
  <<JpaRepository>>
}
class AdminRepository {
  <<JpaRepository>>
}
class UserRepository {
  <<JpaRepository>>
}
class AuditLogRepository {
  <<JpaRepository>>
}

PatientController --> IPatientService
DoctorController --> IDoctorService
AppointmentController --> IAppointmentService
MedicalRecordController --> IMedicalRecordService
BillingController --> IBillingService
AdminController --> IAdminService
PatientDashboardController --> IPatientService
PatientDashboardController --> IAppointmentService
PatientDashboardController --> IMedicalRecordService
PatientDashboardController --> IBillingService
AuthController --> FileUserRepository
AuthController --> FilePatientRepository
AuthController --> FileDoctorRepository

IPatientService <|.. PatientService
IDoctorService <|.. DoctorService
IMedicalRecordService <|.. MedicalRecordService
IBillingService <|.. BillingService
IAdminService <|.. AdminService

PatientService --> FilePatientRepository
PatientService --> FileUserRepository
PatientService --> FileAppointmentRepository
DoctorService --> FileDoctorRepository
DoctorService --> FileUserRepository
DoctorService --> FileAppointmentRepository
MedicalRecordService --> FileMedicalRecordRepository
MedicalRecordService --> FilePatientRepository
MedicalRecordService --> FileDoctorRepository
BillingService --> FileBillingRepository
BillingService --> FileAppointmentRepository
AdminService --> FileAdminRepository
AdminService --> FileUserRepository

FilePatientRepository --> FileStorageUtil
FileDoctorRepository --> FileStorageUtil
FileAppointmentRepository --> FileStorageUtil
FileMedicalRecordRepository --> FileStorageUtil
FileBillingRepository --> FileStorageUtil
FileAdminRepository --> FileStorageUtil
FileUserRepository --> FileStorageUtil
FileAuditRepository --> FileStorageUtil
```

## Notes

- `AuthUser` in the diagram represents `com.medicalapp.auth.entity.User`; the alias avoids confusion with `com.medicalapp.common.model.User`.
- The `model` package contains older serializable `Patient`, `Doctor`, and `Appointment` classes. The active repositories, services, and controllers mainly use the JPA-style classes under each `entity` package.
- `AppointmentController` and `PatientDashboardController` reference `IAppointmentService`, but the matching service files were not present in the scanned source tree.
