package com.edupredict.repository;

import com.edupredict.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByRiskLevel(Student.RiskLevel level);
}
