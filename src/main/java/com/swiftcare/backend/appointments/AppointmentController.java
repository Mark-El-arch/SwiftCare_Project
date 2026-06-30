package com.swiftcare.backend.appointments;
import com.swiftcare.backend.queue.queueEntry;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public appointments bookAppointment(@RequestBody com.swiftcare.backend.appointments.AppointmentRequest request) {
        return appointmentService.bookAppointment(request);
    }
    @GetMapping("/{id}/queue")
    public queueEntry getQueuePosition(@PathVariable Long id) {
        return appointmentService.getQueuePosition(id);
    }

    @PutMapping("/{id}/cancel")
    public appointments cancelAppointment(@PathVariable Long id) {
        return appointmentService.cancelAppointment(id);
    }
    @GetMapping
    public java.util.List<appointments> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }
    @GetMapping("/patient/{patientName}")
    public java.util.List<appointments> getAppointmentsByPatient(
            @PathVariable String patientName
    ) {
        return appointmentService.getAppointmentsByPatient(patientName);
    }
    @GetMapping("/check-availability")
    public boolean checkAvailability(
            @RequestParam Long departmentId,
            @RequestParam java.time.LocalDateTime scheduledTime
    ) {
        return appointmentService.isSlotAvailable(departmentId, scheduledTime);
    }
}