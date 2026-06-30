package com.swiftcare.backend.appointments;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentsRepository
        extends JpaRepository<appointments, Long> {
    List<appointments> findByPatientNameIgnoreCase(String patientName);
    boolean existsByDepartmentIdAndScheduledTimeAndStatusNot(
            Long departmentId,
            java.time.LocalDateTime scheduledTime,
            String status
    );
    boolean existsByDepartmentIdAndScheduledTimeBetweenAndStatusNot(
            Long departmentId,
            java.time.LocalDateTime start,
            java.time.LocalDateTime end,
            String status
    );
}


