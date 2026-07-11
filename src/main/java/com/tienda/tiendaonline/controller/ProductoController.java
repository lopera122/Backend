package com.tienda.tiendaonline.controller;

import com.tienda.tiendaonline.model.Categoria;
import com.tienda.tiendaonline.model.Producto;
import com.tienda.tiendaonline.repository.CategoriaRepository;
import com.tienda.tiendaonline.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    // =============================================
    // GET / → redirige a /productos
    // =============================================
    @GetMapping("/")
    public String home() {
        return "redirect:/productos";
    }

    // =============================================
    // GET /productos → todos los productos por categoría
    // =============================================
    @GetMapping("/productos")
    public String listarProductos(@RequestParam(value = "categoriaId", required = false) Long categoriaId,
                                  @RequestParam(value = "buscar", required = false) String buscar,
                                  Model model) {

        List<Categoria> categorias = categoriaRepository.findByActivoTrue();
        List<Producto> productos;

        if (buscar != null && !buscar.isBlank()) {
            // Búsqueda por nombre
            productos = productoRepository.findByNombreContainingIgnoreCaseAndActivoTrue(buscar);
            model.addAttribute("buscar", buscar);
        } else if (categoriaId != null) {
            // Filtrar por categoría
            productos = productoRepository.findByCategoriaIdAndActivoTrue(categoriaId);
            model.addAttribute("categoriaSeleccionada", categoriaId);
        } else {
            // Todos los productos activos
            productos = productoRepository.findByActivoTrue();
        }

        model.addAttribute("categorias", categorias);
        model.addAttribute("productos", productos);
        return "index";
    }

    // =============================================
    // GET /productos/categoria/{id}
    // =============================================
    @GetMapping("/productos/categoria/{id}")
    public String productosPorCategoria(@PathVariable Long id, Model model) {
        List<Categoria> categorias = categoriaRepository.findByActivoTrue();
        List<Producto> productos = productoRepository.findByCategoriaIdAndActivoTrue(id);

        model.addAttribute("categorias", categorias);
        model.addAttribute("productos", productos);
        model.addAttribute("categoriaSeleccionada", id);
        return "index";
    }
}