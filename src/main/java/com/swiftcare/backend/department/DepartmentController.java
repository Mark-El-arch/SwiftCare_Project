package com.swiftcare.backend.department;

import com.swiftcare.backend.queue.queueEntry;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public List<department> getDepartments() {
        return departmentService.getAllDepartments();
    }

    @GetMapping("/{id}/queue")
    public List<queueEntry> getDepartmentQueue(@PathVariable Long id) {
        return departmentService.getDepartmentQueue(id);
    }
    @GetMapping("/{id}/slots")
    public List<String> getDepartmentSlots(@PathVariable Long id) {
        return departmentService.getDepartmentSlots(id);
    }

    @PostMapping
    public department createDepartment(@RequestBody department department) {
        return departmentService.createDepartment(department);
    }

    @PutMapping("/{id}")
    public department updateDepartment(
            @PathVariable Long id,
            @RequestBody department department
    ) {
        return departmentService.updateDepartment(id, department);
    }

    @DeleteMapping("/{id}")
    public void deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
    }
}
