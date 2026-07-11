package com.tienda.tiendaonline.repository;

import com.tienda.tiendaonline.model.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Administrador, Long> {

    Optional<Administrador> findByEmail(String email);

    boolean existsByEmail(String email);
}