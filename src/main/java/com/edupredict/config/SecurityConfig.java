package com.edupredict.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. AUTHORIZATION RULES
            .authorizeHttpRequests(auth -> auth
                // Allow everyone to see these specific files:
                .requestMatchers("/login.html", "/css/**", "/js/**", "/images/**", "/").permitAll()
                // Everything else (like /dashboard) requires a password:
                .anyRequest().authenticated()
            )
            
            // 2. CUSTOM LOGIN FORM CONFIG
            .formLogin(form -> form
                .loginPage("/login.html")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/dashboard.html", true)
                .permitAll()
            )
            
            // 3. LOGOUT CONFIG
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login.html")
                .permitAll()
            )
            
            // 4. DISABLE CSRF (For development simplicity)
            .csrf(csrf -> csrf.disable());

        return http.build();
    }

    // ✅ THIS IS THE MISSING PIECE!
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}