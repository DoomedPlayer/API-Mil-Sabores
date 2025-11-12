package com.example.test.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig  implements WebMvcConfigurer{

    @Value("${file.upload-dir}")
    private String UPLOAD_DIR;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        String resourceLocation = "file:" + UPLOAD_DIR;
        registry.addResourceHandler("/images/**")
                .addResourceLocations(resourceLocation);
    }
}
