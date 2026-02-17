package com.edupredict;

import com.edupredict.model.User;
import com.edupredict.model.Student;
import com.edupredict.repository.UserRepository;
import com.edupredict.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

@Configuration
public class DatabaseLoader {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, StudentRepository studentRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            System.out.println("🚀 SECURING ADMIN ACCOUNT...");

            // --- 1. ADMIN USER SETUP ---
            // FIX 1: Added .orElse(null) to handle the Optional
            User admin = userRepository.findByUsername("admin").orElse(null);
            if (admin == null) {
                admin = new User();
                admin.setUsername("admin");
            }
            admin.setRole("ADMIN"); 
            admin.setPassword(passwordEncoder.encode("admin123")); 
            userRepository.save(admin);
            System.out.println("✅ ADMIN SECURED! You can now log in with: admin / admin123");

            // --- 2. AUTO-POPULATE STUDENTS ---
          
        };
    }
}