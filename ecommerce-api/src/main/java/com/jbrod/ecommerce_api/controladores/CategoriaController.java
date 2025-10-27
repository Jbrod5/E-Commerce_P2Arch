package com.jbrod.ecommerce_api.controladores;

import com.jbrod.ecommerce_api.modelos.productos.Categoria;
import com.jbrod.ecommerce_api.servicios.ProductoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para manejar las categorías de productos.
 * Provee un endpoint público (o autenticado) para listar las categorías disponibles.
 */
@RestController
@RequestMapping("/api/utilidades")
public class CategoriaController {

    private final ProductoService productoServicio;

    public CategoriaController(ProductoService productoServicio) {
        this.productoServicio = productoServicio;
    }

    /**
     * Endpoint para obtener la lista de categorías disponibles.
     * URL: GET /api/categorias
     */
    @GetMapping("/categorias")
    public ResponseEntity<List<Categoria>> obtenerCategorias() {
        List<Categoria> categorias = productoServicio.obtenerCategorias();
        return ResponseEntity.ok(categorias);
    }
}
