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
import org.springframework.http.HttpHeaders; // <-- ADDED FOR CSV DOWNLOAD
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

    // 1. Get All Students (Paginated)
    @GetMapping
    public Page<Student> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return studentRepository.findAll(
            PageRequest.of(page, size, Sort.by("id").descending())
        );
    }

    // 2. Get Single Student
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        return ResponseEntity.ok(student);
    }

    // 3. Create Student (NOW WITH AUTO-PREDICTION)
    @PostMapping
    public Student createStudent(@RequestBody Student student) {
        // Run AI immediately before saving
        runAiPrediction(student);
        return studentRepository.save(student);
    }

    // 4. Trigger Prediction Manually (Updates existing students)
    @PostMapping("/{id}/predict")
    public ResponseEntity<Student> predictPerformance(@PathVariable Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        runAiPrediction(student); // Re-run AI
        return ResponseEntity.ok(studentRepository.save(student));
    }

    // 5. Delete Student
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ==========================================
    // 6. EXPORT STUDENTS TO CSV EXCEL FILE
    // ==========================================
    @GetMapping(value = "/report/csv", produces = "text/csv")
    public ResponseEntity<String> exportStudentsCSV() {
        // Get all students
        List<Student> students = studentRepository.findAll();
        
        // Build the CSV Header
        StringBuilder csv = new StringBuilder();
        csv.append("Student ID,Full Name,Current GPA,Attendance Rate,Risk Level\n");
        
        // Loop through and add data rows
        for (Student s : students) {
            String fullName = s.getFirstName() + " " + s.getLastName();
            String risk = (s.getRiskLevel() != null) ? s.getRiskLevel().toString() : "PENDING";
            
            csv.append(s.getStudentId()).append(",")
               .append(fullName).append(",")
               .append(s.getCurrentGpa()).append(",")
               .append(s.getAttendancePercentage()).append("%,")
               .append(risk).append("\n");
        }
        
        // Return as downloadable file
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"EduPredict_Risk_Report.csv\"")
                .body(csv.toString());
    }

   // --- HELPER: The AI Logic (With Java Fallback!) ---
    private void runAiPrediction(Student student) {
        try {
            // 1. Locate the Python script
            File scriptFile = new ClassPathResource("ml/prediction_model.py").getFile();
            
            // 2. Run Python
            ProcessBuilder processBuilder = new ProcessBuilder(
                "python", 
                scriptFile.getAbsolutePath(), 
                String.valueOf(student.getCurrentGpa()), 
                String.valueOf(student.getAttendancePercentage())
            );
            
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // 3. Read Output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            // 4. Parse JSON
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
            // ====================================================
            // 🚀 THE FALLBACK BRAIN: If Python fails, Java takes over!
            // ====================================================
            System.err.println("⚠️ Python AI failed. Using built-in Java prediction algorithm...");
            
            double gpa = student.getCurrentGpa();
            double attendance = student.getAttendancePercentage();
            
            // Calculate a rough prediction score
            double predicted = (gpa * 0.6) + ((attendance / 10.0) * 0.4);
            student.setPredictedGrade(predicted);

            // Set Risk Level based on strict thresholds
            if (gpa < 5.0 || attendance < 65) {
                student.setRiskLevel(Student.RiskLevel.HIGH);
            } else if (gpa < 7.5 || attendance < 80) {
                student.setRiskLevel(Student.RiskLevel.MEDIUM);
            } else {
                student.setRiskLevel(Student.RiskLevel.LOW);
            }
        }
    }

    // ==========================================
    // 7. BULK AI PREDICTION (Update all at once!)
    // ==========================================
    @PostMapping("/predict-all")
    public ResponseEntity<String> predictAllStudents() {
        List<Student> allStudents = studentRepository.findAll();
        
        for (Student student : allStudents) {
            runAiPrediction(student); // Run the AI for every single student
        }
        
        studentRepository.saveAll(allStudents); // Save the updated grades to the cloud
        return ResponseEntity.ok("{\"status\": \"success\"}");
    }
}