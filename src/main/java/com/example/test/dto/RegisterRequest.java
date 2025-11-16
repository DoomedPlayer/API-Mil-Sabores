package com.example.test.dto;

import java.time.LocalDate;

public record RegisterRequest(String nombre, String email, String password,LocalDate fechaNacimiento) {}