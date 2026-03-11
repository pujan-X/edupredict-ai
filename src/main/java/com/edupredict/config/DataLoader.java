package com.edupredict.config;

import com.edupredict.model.User;
import com.edupredict.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Optional; 
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        Optional<User> adminOptional = userRepository.findByUsername("admin");

        User admin;

        if (adminOptional.isPresent()) {
            admin = adminOptional.get();
        } else {
            admin = new User();
            admin.setUsername("admin");
            admin.setRole("ROLE_ADMIN");
        }

        admin.setPassword(passwordEncoder.encode("admin123")); 
        
        userRepository.save(admin);
        
        System.out.println("✅ SECURITY ALERT: Admin password forcefully reset to 'admin123'");
    }
}
