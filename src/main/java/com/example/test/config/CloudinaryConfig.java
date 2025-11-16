package com.example.test.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {
    
    @Bean
    public Cloudinary cloudinary(){
        return new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "dlqvbm39g",
            "api_key", "435183473327498",
            "api_secret", "uGXf6jH3Tltqu15ZVkgBlhPPEY0",
            "secure", true
        ));
    }
}
