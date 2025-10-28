package com.mhms.medisynapse.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FileUploadConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:uploads/lab-reports}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Get absolute path to upload directory
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        String uploadLocation = "file:" + uploadPath.toString() + "/";

        // Map /lab-reports/** URLs to the actual upload directory
        registry.addResourceHandler("/lab-reports/**")
                .addResourceLocations(uploadLocation)
                .setCachePeriod(3600); // Cache for 1 hour

        // Log the configuration
        System.out.println("📁 Static file serving configured:");
        System.out.println("   URL Pattern: /lab-reports/**");
        System.out.println("   File Location: " + uploadLocation);
    }
}

