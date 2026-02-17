package com.edupredict.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;

    @Column(unique = true)
    private String studentId; // e.g., "STD-2024-001"

    private Double currentGpa;
    private Integer attendancePercentage;
    
    // Risk Level: LOW, MEDIUM, HIGH
    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel; 
    
    // For ML Prediction results
    private Double predictedGrade;
    private Double predictionConfidence;

    // This defines the options for Risk Level
    public enum RiskLevel {
        LOW, MEDIUM, HIGH
    }
    // --- HELPER METHOD TO FIX THE ERROR ---
    public String getName() {
        return this.firstName + " " + this.lastName;
    }
}