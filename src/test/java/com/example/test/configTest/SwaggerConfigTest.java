package com.example.test.configTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class SwaggerConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void customOpenAPIBeanExistsAndConfiguredCorrectly() {
        OpenAPI openAPI = applicationContext.getBean(OpenAPI.class);
        assertNotNull(openAPI, "The OpenAPI bean should not be null");

        Info info = openAPI.getInfo();
        assertNotNull(info, "The Info object should not be null");

        assertEquals("Proyecto Cotizacion", info.getTitle(), "The title should match 'Proyecto Cotizacion'");
        assertEquals("1.0", info.getVersion(), "The version should match '1.0'");
        assertEquals("Documentacion de la API para el sistema de cotizacion", info.getDescription(), "The description should match 'Documentacion de la API para el sistema de cotizacion'");
    }
}