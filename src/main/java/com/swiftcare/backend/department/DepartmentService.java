package com.swiftcare.backend.department;

import com.swiftcare.backend.queue.QueueEntryRepository;
import com.swiftcare.backend.queue.queueEntry;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final QueueEntryRepository queueEntryRepository;

    public DepartmentService(
            DepartmentRepository departmentRepository,
            QueueEntryRepository queueEntryRepository
    ) {
        this.departmentRepository = departmentRepository;
        this.queueEntryRepository = queueEntryRepository;
    }

    public List<department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public List<queueEntry> getDepartmentQueue(Long departmentId) {
        department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        return queueEntryRepository.findByDepartmentOrderByPositionAsc(department);
    }

    public List<String> getDepartmentSlots(Long departmentId) {
        department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        int averageMinutes = department.getAverageConsultationMinutes();

        return List.of(
                "09:00 - " + averageMinutes + " mins",
                "10:00 - " + averageMinutes + " mins",
                "11:00 - " + averageMinutes + " mins",
                "12:00 - " + averageMinutes + " mins",
                "14:00 - " + averageMinutes + " mins"
        );
    }

    public department createDepartment(department newDepartment) {
        return departmentRepository.save(newDepartment);
    }

    public department updateDepartment(Long id, department updatedDepartment) {

        department existingDepartment = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        existingDepartment.setName(updatedDepartment.getName());
        existingDepartment.setAverageConsultationMinutes(
                updatedDepartment.getAverageConsultationMinutes()
        );
        existingDepartment.setHospital(updatedDepartment.getHospital());

        return departmentRepository.save(existingDepartment);
    }

    public void deleteDepartment(Long id) {

        department existingDepartment = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        departmentRepository.delete(existingDepartment);
    }
}