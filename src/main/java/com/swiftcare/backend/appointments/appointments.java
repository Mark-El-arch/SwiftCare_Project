package com.swiftcare.backend.appointments;

import com.swiftcare.backend.department.department;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class appointments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "patient_name")
    private String patientName;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private department department;

    @Column(name = "scheduled_time")
    private LocalDateTime scheduledTime;

    @Column(name = "severity_score")
    private Integer severityScore;

    private Boolean premium;

    private String status;
}