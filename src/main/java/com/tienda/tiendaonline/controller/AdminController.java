package com.tienda.tiendaonline.controller;

import com.tienda.tiendaonline.model.Categoria;
import com.tienda.tiendaonline.model.Producto;
import com.tienda.tiendaonline.repository.CategoriaRepository;
import com.tienda.tiendaonline.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class AdminController {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    // =============================================
    // RUTA ABSOLUTA donde vive tu frontend con Live Server
    // CAMBIA ESTO si mueves la carpeta del proyecto
    // =============================================
    private static final String RUTA_IMAGENES_FRONTEND = "D:\\clase de aplicaciones\\proyecto\\fronted\\images\\productos";

    // ====================== CATEGORIAS ======================

    @PostMapping("/categorias")
    public ResponseEntity<Categoria> crearCategoria(@RequestBody Categoria categoria) {
        Categoria nueva = categoriaRepository.save(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    @GetMapping("/categorias")
    public List<Categoria> listarCategorias() {
        return categoriaRepository.findAll();
    }

    // ====================== PRODUCTOS CON IMAGEN ======================

    @PostMapping("/productos")
    public ResponseEntity<Producto> crearProducto(
            @RequestParam("nombre") String nombre,
            @RequestParam("precio") String precio,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("stock") Integer stock,
            @RequestParam("categoriaId") Long categoriaId,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen) {

        try {
            String nombreArchivo = null;

            if (imagen != null && !imagen.isEmpty()) {
                nombreArchivo = System.currentTimeMillis() + "_" + limpiarNombre(imagen.getOriginalFilename());

                Path uploadPath = Paths.get(RUTA_IMAGENES_FRONTEND);
                Files.createDirectories(uploadPath);

                Path filePath = uploadPath.resolve(nombreArchivo);
                Files.copy(imagen.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            Producto producto = new Producto();
            producto.setNombre(nombre);
            producto.setPrecio(new java.math.BigDecimal(precio));
            producto.setDescripcion(descripcion);
            producto.setStock(stock != null ? stock : 10);
            producto.setImagenUrl(nombreArchivo);
            producto.setCategoria(categoriaRepository.findById(categoriaId).orElseThrow());

            Producto nuevo = productoRepository.save(producto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    private String limpiarNombre(String nombreOriginal) {
        if (nombreOriginal == null) return "imagen.jpg";
        return nombreOriginal.replaceAll("[^a-zA-Z0-9.\\-]", "_");
    }

    @GetMapping("/productos")
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    // ====================== ELIMINAR ======================

    @DeleteMapping("/categorias/{id}")
    @Transactional
    public ResponseEntity<Map<String, String>> eliminarCategoria(@PathVariable Long id) {
        try {
            productoRepository.deleteByCategoriaId(id);
            categoriaRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Categoria y productos eliminados"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error al eliminar"));
        }
    }

    @DeleteMapping("/productos/{id}")
    @Transactional
    public ResponseEntity<Map<String, String>> eliminarProducto(@PathVariable Long id) {
        productoRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Producto eliminado"));
    }
}