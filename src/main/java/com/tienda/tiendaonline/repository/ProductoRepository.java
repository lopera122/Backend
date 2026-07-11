package com.tienda.tiendaonline.repository;

import com.tienda.tiendaonline.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Todos los productos activos
    List<Producto> findByActivoTrue();

    // Productos activos por categoría
    List<Producto> findByCategoriaIdAndActivoTrue(Long categoriaId);

    // Buscar por nombre (para buscador)
    List<Producto> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);

    // ← Agrega esta línea
    void deleteByCategoriaId(Long categoriaId);
}