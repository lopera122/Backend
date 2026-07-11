package com.tienda.tiendaonline.controller;

import com.tienda.tiendaonline.dto.LoginRequest;
import com.tienda.tiendaonline.model.Administrador;
import com.tienda.tiendaonline.model.Usuario;
import com.tienda.tiendaonline.repository.AdminRepository;
import com.tienda.tiendaonline.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AdminRepository adminRepository;   // ← Correcto

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ===========================
    // REGISTRO DE USUARIOS
    // ===========================
    @PostMapping("/register")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario usuario) {

        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Ya existe una cuenta con ese correo.");
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setActivo(true);

        usuarioRepository.save(usuario);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Usuario registrado correctamente.");
    }

    // ===========================
    // LOGIN USUARIOS
    // ===========================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail()).orElse(null);

        if (usuario == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "No existe cuenta registrada con ese correo"));
        }

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "La contraseña está incorrecta"));
        }

        return ResponseEntity.ok(usuario);
    }

    // ===========================
    // LOGIN ADMINISTRADORES
    // ===========================
    @PostMapping("/admin/login")
    public ResponseEntity<?> loginAdmin(@RequestBody LoginRequest request) {

        Administrador admin = adminRepository.findByEmail(request.getEmail()).orElse(null);

        if (admin == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "No existe cuenta de administrador con ese correo"));
        }

        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "La contraseña de administrador es incorrecta"));
        }

        if (!admin.getActivo()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "La cuenta de administrador está desactivada"));
        }

        return ResponseEntity.ok(admin);
    }
    // ===========================
    // REGISTRO DE ADMINISTRADOR (para desarrollo)
    // ===========================
    @PostMapping("/admin/register")
    public ResponseEntity<?> registrarAdministrador(@RequestBody Administrador admin) {

        if (adminRepository.existsByEmail(admin.getEmail())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Ya existe un administrador con ese correo."));
        }

        // Encriptar contraseña
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        admin.setActivo(true);

        Administrador nuevoAdmin = adminRepository.save(admin);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Administrador registrado correctamente",
                        "id", nuevoAdmin.getId(),
                        "email", nuevoAdmin.getEmail()
                ));
    }
}