package com.tienda.tiendaonline.repository;

import com.tienda.tiendaonline.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByActivoTrue();

    boolean existsByNombre(String nombre);
}