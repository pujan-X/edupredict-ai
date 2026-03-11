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
    private String studentId;
    private Double currentGpa;
    private Integer attendancePercentage;
    
    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel; 
    
    private Double predictedGrade;
    private Double predictionConfidence;

    public enum RiskLevel {
        LOW, MEDIUM, HIGH
    }
    public String getName() {
        return this.firstName + " " + this.lastName;
    }
}
