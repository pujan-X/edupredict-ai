package com.edupredict.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Rule 1: localhost:8080 -> login.html
        registry.addViewController("/").setViewName("forward:/login.html");
        
        // Rule 2: localhost:8080/login -> login.html
        registry.addViewController("/login").setViewName("forward:/login.html");

        // Rule 3: localhost:8080/dashboard -> dashboard.html
        registry.addViewController("/dashboard").setViewName("forward:/dashboard.html");
    }
}