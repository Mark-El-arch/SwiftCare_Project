package com.swiftcare.backend.symptom;

public class SymptomRequest {
    private Long patientId;
    private String symptoms;
    private String healthProfileSnapshot;

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public String getSymptoms() { return symptoms; }
    public void setSymptoms(String symptoms) { this.symptoms = symptoms; }
    public String getHealthProfileSnapshot() { return healthProfileSnapshot; }
    public void setHealthProfileSnapshot(String healthProfileSnapshot) { this.healthProfileSnapshot = healthProfileSnapshot; }
}