package com.medicalapp.appointment.repository;

import com.medicalapp.appointment.entity.Appointment;
import com.medicalapp.common.storage.FileStorageUtil;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class FileAppointmentRepository {
    private final FileStorageUtil<Appointment> storage = new FileStorageUtil<>("appointments.txt");

    public List<Appointment> findAll() {
        return storage.readFromFile(this::mapToAppointment);
    }

    public Optional<Appointment> findById(Long id) {
        return findAll().stream().filter(a -> a.getId().equals(id)).findFirst();
    }

    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }

    public Appointment save(Appointment appointment) {
        List<Appointment> appointments = findAll();
        if (appointment.getId() == null) {
            appointment.setId(System.currentTimeMillis());
            appointments.add(appointment);
        } else {
            appointments = appointments.stream()
                    .map(a -> a.getId().equals(appointment.getId()) ? appointment : a)
                    .collect(Collectors.toList());
        }
        storage.writeToFile(appointments, this::mapToString);
        return appointment;
    }

    public void deleteById(Long id) {
        List<Appointment> appointments = findAll().stream()
                .filter(a -> !a.getId().equals(id))
                .collect(Collectors.toList());
        storage.writeToFile(appointments, this::mapToString);
    }

    public List<Appointment> findByPatientId(Long patientId) {
        return findAll().stream()
                .filter(a -> a.getPatientId() != null && a.getPatientId().equals(patientId))
                .collect(Collectors.toList());
    }

    public List<Appointment> findByDoctorId(Long doctorId) {
        return findAll().stream()
                .filter(a -> a.getDoctorId() != null && a.getDoctorId().equals(doctorId))
                .collect(Collectors.toList());
    }

    private String mapToString(Appointment a) {
        return String.format("%d,%s,%s,%s,%s,%s,%s",
                a.getId(),
                a.getPatientId() != null ? a.getPatientId().toString() : "0",
                a.getDoctorId() != null ? a.getDoctorId().toString() : "0",
                a.getAppointmentDate() != null ? a.getAppointmentDate().toString() : LocalDate.now().toString(),
                a.getAppointmentTime() != null ? a.getAppointmentTime().toString() : LocalTime.now().toString(),
                clean(a.getReason(), "N/A"),
                a.getStatus() != null ? a.getStatus().toString() : "PENDING");
    }

    private String clean(String input, String def) {
        if (input == null || input.trim().isEmpty()) return def;
        return input.replace(",", " ").replace("\n", " ").replace("\r", " ").trim();
    }

    private Appointment mapToAppointment(String line) {
        String[] parts = line.split(",");
        Appointment a = new Appointment();
        if (parts.length < 2) return null;

        try {
            a.setId(Long.parseLong(parts[0]));
            if (parts.length > 1 && !"N/A".equals(parts[1])) a.setPatientId(Long.parseLong(parts[1]));
            if (parts.length > 2 && !"N/A".equals(parts[2])) a.setDoctorId(Long.parseLong(parts[2]));
            if (parts.length > 3 && !"N/A".equals(parts[3])) a.setAppointmentDate(LocalDate.parse(parts[3]));
            if (parts.length > 4 && !"N/A".equals(parts[4])) a.setAppointmentTime(LocalTime.parse(parts[4]));
            if (parts.length > 5) a.setReason(parts[5]);
            if (parts.length > 6 && !"N/A".equals(parts[6])) {
                try {
                    a.setStatus(Appointment.AppointmentStatus.valueOf(parts[6]));
                } catch (Exception e) {
                    a.setStatus(Appointment.AppointmentStatus.PENDING);
                }
            }
            return a;
        } catch (Exception e) {
            return null;
        }
    }
}



