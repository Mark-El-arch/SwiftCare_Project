package com.swiftcare.backend.queue;

import com.swiftcare.backend.appointments.appointments;
import com.swiftcare.backend.department.department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QueueEntryRepository extends JpaRepository<queueEntry, Long> {

    // Finds the queue record for one appointment
    Optional<queueEntry> findByAppointment(appointments appointment);

    // Displays the current saved queue order
    List<queueEntry> findByDepartmentOrderByPositionAsc(department department);

    // Counts how many patients are in a department queue
    long countByDepartment(department department);

    // Gets all queue entries for a department before recalculating smart positions
    List<queueEntry> findByDepartment(department department);
}