package com.tienda.tiendaonline.service;

import com.tienda.tiendaonline.model.Usuario;
import com.tienda.tiendaonline.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario autenticar(String email, String password) {

        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);

        if (usuarioOptional.isEmpty()) {
            return null;
        }

        Usuario usuario = usuarioOptional.get();

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            return null;
        }

        return usuario;
    }
}