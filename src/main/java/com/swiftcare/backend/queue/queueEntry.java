package com.swiftcare.backend.queue;

import com.swiftcare.backend.appointments.appointments;
import com.swiftcare.backend.department.department;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "queue_entry")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class queueEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "appointment_id", nullable = false)
    private appointments appointment;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private department department;

    private Integer position;

    @Column(name = "estimated_call_time")
    private LocalDateTime estimatedCallTime;
}