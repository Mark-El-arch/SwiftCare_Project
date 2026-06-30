package com.swiftcare.backend.appointments;

import java.time.LocalDateTime;

public class AppointmentRequest {

    private String patientName;
    private Long departmentId;
    private LocalDateTime scheduledTime;
    private Integer severityScore;
    private Boolean premium;

    public String getPatientName() {
        return patientName;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public Integer getSeverityScore() {
        return severityScore;
    }

    public Boolean getPremium() {
        return premium;
    }
}