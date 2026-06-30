package com.swiftcare.backend.appointments;

import com.swiftcare.backend.department.department;
import com.swiftcare.backend.department.DepartmentRepository;
import com.swiftcare.backend.queue.QueueService;
import com.swiftcare.backend.queue.queueEntry;
import com.swiftcare.backend.queue.QueueEntryRepository;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class AppointmentService {

    private final AppointmentsRepository appointmentRepository;
    private final DepartmentRepository departmentRepository;
    private final QueueEntryRepository queueEntryRepository;
    private final QueueService queueService;

    public AppointmentService(
            AppointmentsRepository appointmentRepository,
            DepartmentRepository departmentRepository,
            QueueEntryRepository queueEntryRepository,
            QueueService queueService
    ) {
        this.appointmentRepository = appointmentRepository;
        this.departmentRepository = departmentRepository;
        this.queueEntryRepository = queueEntryRepository;
        this.queueService = queueService;
    }

    public appointments bookAppointment(com.swiftcare.backend.appointments.AppointmentRequest request) {
        boolean slotTaken = appointmentRepository.existsByDepartmentIdAndScheduledTimeAndStatusNot(
                request.getDepartmentId(),
                request.getScheduledTime(),
                "CANCELLED"
        );

        if (slotTaken) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    "This appointment time is already booked. Please choose another time."
            );
        }
        department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        appointments appointment = new appointments();
        appointment.setPatientName(request.getPatientName());
        appointment.setDepartment(department);
        appointment.setScheduledTime(request.getScheduledTime());
        appointment.setSeverityScore(request.getSeverityScore());
        appointment.setPremium(request.getPremium());
        appointment.setStatus("BOOKED");

        appointments savedAppointment = appointmentRepository.save(appointment);

        queueEntry queueEntry = new queueEntry();
        queueEntry.setAppointment(savedAppointment);
        queueEntry.setDepartment(department);

        long currentQueueSize = queueEntryRepository.countByDepartment(department);
        queueEntry.setPosition((int) currentQueueSize + 1);

        queueEntryRepository.save(queueEntry);

// Recalculate queue positions and ETA
        queueService.recalculateDepartmentQueue(department.getId());

        return savedAppointment;

    }
    public queueEntry getQueuePosition(Long appointmentId) {
        appointments appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        return queueEntryRepository.findByAppointment(appointment)
                .orElseThrow(() -> new RuntimeException("Queue entry not found"));
    }
    public appointments cancelAppointment(Long id) {
        appointments appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setStatus("CANCELLED");

        return appointmentRepository.save(appointment);
    }
    public List<appointments> getAllAppointments() {
        return appointmentRepository.findAll();
    }
    public java.util.List<appointments> getAppointmentsByPatient(String patientName) {
        return appointmentRepository.findByPatientNameIgnoreCase(patientName);
    }
    public boolean isSlotAvailable(Long departmentId, java.time.LocalDateTime scheduledTime) {
        java.time.LocalDateTime start = scheduledTime.withSecond(0).withNano(0);
        java.time.LocalDateTime end = start.plusMinutes(1);

        boolean slotTaken =
                appointmentRepository.existsByDepartmentIdAndScheduledTimeBetweenAndStatusNot(
                        departmentId,
                        start,
                        end,
                        "CANCELLED"
                );

        return !slotTaken;
    }

}