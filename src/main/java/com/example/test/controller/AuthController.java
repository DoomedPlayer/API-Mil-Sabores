package com.example.test.controller;

import com.example.test.dto.AuthRequest;
import com.example.test.dto.AuthResponse;
import com.example.test.dto.RegisterRequest;
import com.example.test.model.Role;
import com.example.test.model.Usuario;
import com.example.test.repository.UsuarioRepository;
import com.example.test.services.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
            Usuario usuario = usuarioRepository.findByEmail(request.email())
                    .orElseThrow();
            String token = jwtService.generateToken(usuario);
            return ResponseEntity.ok(new AuthResponse(token, usuario.getRole().name()));
        
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Credenciales incorrectas");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre());
        usuario.setEmail(request.email());
        usuario.setContraseña(passwordEncoder.encode(request.password()));
        usuario.setFechaNacimiento(request.fechaNacimiento());
        usuario.setRole(Role.Usuario);

        usuarioRepository.save(usuario);

        String token = jwtService.generateToken(usuario);

        return ResponseEntity.ok(new AuthResponse(token, usuario.getRole().name()));
    }
}