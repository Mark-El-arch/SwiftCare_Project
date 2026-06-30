package com.swiftcare.backend.department;

import com.swiftcare.backend.hospital.hospital;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "department")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "average_consultation_minutes")
    private Integer averageConsultationMinutes;

    @ManyToOne
    @JoinColumn(name = "hospital_id")
    private hospital hospital;
}