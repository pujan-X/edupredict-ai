package com.edupredict.config;

import com.edupredict.model.User;
import com.edupredict.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Optional; // ✅ Added Import

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 1. Get the "Box" (Optional) from the database
        Optional<User> adminOptional = userRepository.findByUsername("admin");

        User admin;

        // 2. Check if the User exists inside the box
        if (adminOptional.isPresent()) {
            admin = adminOptional.get(); // Unwrap the existing user
        } else {
            // Box is empty, create a brand new user
            admin = new User();
            admin.setUsername("admin");
            admin.setRole("ROLE_ADMIN");
        }

        // 3. FORCE RESET THE PASSWORD
        admin.setPassword(passwordEncoder.encode("admin123")); 
        
        // 4. Save to Database
        userRepository.save(admin);
        
        System.out.println("✅ SECURITY ALERT: Admin password forcefully reset to 'admin123'");
    }
}