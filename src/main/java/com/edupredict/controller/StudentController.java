package com.edupredict.controller;

import com.edupredict.model.Student;
import com.edupredict.repository.StudentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders; 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping
    public Page<Student> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return studentRepository.findAll(
            PageRequest.of(page, size, Sort.by("id").descending())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        return ResponseEntity.ok(student);
    }

    @PostMapping
    public Student createStudent(@RequestBody Student student) {
        runAiPrediction(student);
        return studentRepository.save(student);
    }

    @PostMapping("/{id}/predict")
    public ResponseEntity<Student> predictPerformance(@PathVariable Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        runAiPrediction(student); 
        return ResponseEntity.ok(studentRepository.save(student));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    
    @GetMapping(value = "/report/csv", produces = "text/csv")
    public ResponseEntity<String> exportStudentsCSV() {
        List<Student> students = studentRepository.findAll();
        
        StringBuilder csv = new StringBuilder();
        csv.append("Student ID,Full Name,Current GPA,Attendance Rate,Risk Level\n");
        
        for (Student s : students) {
            String fullName = s.getFirstName() + " " + s.getLastName();
            String risk = (s.getRiskLevel() != null) ? s.getRiskLevel().toString() : "PENDING";
            
            csv.append(s.getStudentId()).append(",")
               .append(fullName).append(",")
               .append(s.getCurrentGpa()).append(",")
               .append(s.getAttendancePercentage()).append("%,")
               .append(risk).append("\n");
        }
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"EduPredict_Risk_Report.csv\"")
                .body(csv.toString());
    }

    private void runAiPrediction(Student student) {
        try {
            File scriptFile = new ClassPathResource("ml/prediction_model.py").getFile();
            
            ProcessBuilder processBuilder = new ProcessBuilder(
                "python", 
                scriptFile.getAbsolutePath(), 
                String.valueOf(student.getCurrentGpa()), 
                String.valueOf(student.getAttendancePercentage())
            );
            
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(output.toString());
            
            if (rootNode.has("status") && rootNode.get("status").asText().equals("success")) {
                double predicted = rootNode.get("prediction").asDouble();
                student.setPredictedGrade(predicted);
                
                if (predicted < 5.0) student.setRiskLevel(Student.RiskLevel.HIGH);
                else if (predicted < 7.5) student.setRiskLevel(Student.RiskLevel.MEDIUM);
                else student.setRiskLevel(Student.RiskLevel.LOW);
            } else {
                throw new Exception("Python script did not return success.");
            }

        } catch (Exception e) {
            
            System.err.println("⚠️ Python AI failed. Using built-in Java prediction algorithm...");
            
            double gpa = student.getCurrentGpa();
            double attendance = student.getAttendancePercentage();
            
            double predicted = (gpa * 0.6) + ((attendance / 10.0) * 0.4);
            student.setPredictedGrade(predicted);

        
            if (gpa < 5.0 || attendance < 65) {
                student.setRiskLevel(Student.RiskLevel.HIGH);
            } else if (gpa < 7.5 || attendance < 80) {
                student.setRiskLevel(Student.RiskLevel.MEDIUM);
            } else {
                student.setRiskLevel(Student.RiskLevel.LOW);
            }
        }
    }
    @PostMapping("/predict-all")
    public ResponseEntity<String> predictAllStudents() {
        List<Student> allStudents = studentRepository.findAll();
        
        for (Student student : allStudents) {
            runAiPrediction(student); 
        }
        
        studentRepository.saveAll(allStudents); // Save the updated grades to the cloud
        return ResponseEntity.ok("{\"status\": \"success\"}");
    }
}
