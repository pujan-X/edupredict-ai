package com.edupredict.controller;

import com.edupredict.model.User;
import com.edupredict.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // --- ENDPOINT FOR FRONTEND TO KNOW CURRENT USER'S ROLE ---
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Don't send the password hash to the frontend
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }

    // --- ADMIN ONLY ENDPOINTS ---

    // Get all users
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Create new user
   @PostMapping
    public User createUser(@RequestBody User user) {
        // 1. DEBUG LOG: Check what arrived (Remove this line before production!)
        System.out.println("Creating User: " + user.getUsername());
        System.out.println("Raw Password Received: '" + user.getPassword() + "'");

        // 2. ENCRYPT
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        
        // 3. DEBUG LOG: Check the hash
        System.out.println("Encoded Password to Save: " + encodedPassword);

        return userRepository.save(user);
    }

    // Update user role (and optionally password)
   @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setUsername(userDetails.getUsername());
        user.setRole(userDetails.getRole());

        // IMPORTANT: Only update password if the user actually typed a new one
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
             System.out.println("Updating Password for: " + user.getUsername()); // Debug
             user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        
        return ResponseEntity.ok(userRepository.save(user));
    }

    // DELETE USER ENDPOINT
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.ok("User deleted successfully");
        } else {
            return ResponseEntity.status(404).body("User not found");
        }
    }
}