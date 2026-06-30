package com.swiftcare.backend.queue;

import com.swiftcare.backend.department.DepartmentRepository;
import com.swiftcare.backend.department.department;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class QueueService {

    private final QueueEntryRepository queueEntryRepository;
    private final DepartmentRepository departmentRepository;

    public QueueService(
            QueueEntryRepository queueEntryRepository,
            DepartmentRepository departmentRepository
    ) {
        this.queueEntryRepository = queueEntryRepository;
        this.departmentRepository = departmentRepository;
    }

    // Recalculate queue for one department
    public void recalculateDepartmentQueue(Long departmentId) {
        department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        List<queueEntry> entries = queueEntryRepository.findByDepartment(department);

        // Smart sorting:
        // 1. Higher severityScore first
        // 2. Premium patients first
        // 3. Earlier scheduledTime first
        entries.sort(
                Comparator
                        .comparing(
                                (queueEntry entry) -> entry.getAppointment().getSeverityScore(),
                                Comparator.reverseOrder()
                        )
                        .thenComparing(
                                entry -> !entry.getAppointment().getPremium()
                        )
                        .thenComparing(
                                entry -> entry.getAppointment().getScheduledTime()
                        )
        );

        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < entries.size(); i++) {
            queueEntry entry = entries.get(i);

            entry.setPosition(i + 1);

            // Assume each patient takes 15 minutes
            entry.setEstimatedCallTime(now.plusMinutes((long) i * 15));

            queueEntryRepository.save(entry);
        }
    }

    // Recalculate all department queues every 60 seconds
    @Scheduled(fixedRate = 60000)
    public void recalculateAllQueues() {
        List<department> departments = departmentRepository.findAll();

        for (department dept : departments) {
            recalculateDepartmentQueue(dept.getId());
        }
    }
}