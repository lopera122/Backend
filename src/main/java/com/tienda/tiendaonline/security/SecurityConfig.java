package com.tienda.tiendaonline.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll()

                        .requestMatchers("/api/admin/**").permitAll()   // ← Importante

                        .requestMatchers(
                                "/",
                                "/login",
                                "/register",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/index.html",
                                "/admin-dashboard.html"
                        ).permitAll()

                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )

                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable());

        return http.build();
    }
}