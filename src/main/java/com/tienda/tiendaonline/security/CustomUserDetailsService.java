package com.tienda.tiendaonline.security;

import com.tienda.tiendaonline.model.Administrador;
import com.tienda.tiendaonline.model.Usuario;
import com.tienda.tiendaonline.repository.AdminRepository;
import com.tienda.tiendaonline.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // 1. Buscar primero en administradores
        Optional<Administrador> admin = adminRepository.findByEmail(email);
        if (admin.isPresent()) {
            Administrador a = admin.get();
            if (!a.getActivo()) {
                throw new UsernameNotFoundException("Administrador inactivo");
            }
            return new User(
                    a.getEmail(),
                    a.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
        }

        // 2. Buscar en usuarios normales
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        if (usuario.isPresent()) {
            Usuario u = usuario.get();
            if (!u.getActivo()) {
                throw new UsernameNotFoundException("Usuario inactivo");
            }
            return new User(
                    u.getEmail(),
                    u.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_USER"))
            );
        }

        throw new UsernameNotFoundException("No existe una cuenta con el email: " + email);
    }
}