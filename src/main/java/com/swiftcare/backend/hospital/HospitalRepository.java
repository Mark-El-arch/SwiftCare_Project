package com.swiftcare.backend.hospital;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HospitalRepository
        extends JpaRepository<hospital, Long> {
}