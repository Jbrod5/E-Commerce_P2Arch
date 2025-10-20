package com.jbrod.ecommerce_api.controladores;

import com.jbrod.ecommerce_api.dto.EstadoProductoDTO;
import com.jbrod.ecommerce_api.modelos.productos.Producto;
import com.jbrod.ecommerce_api.servicios.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Importación necesaria para protección a nivel de método
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para acciones de Moderación (especialmente la aprobación de productos).
 * Requiere el rol MODERADOR para todos sus endpoints.
 */
@RestController
@RequestMapping("/api/moderador")
public class ModeradorController {

    private final ProductoService productoServicio;

    public ModeradorController(ProductoService productoServicio) {
        this.productoServicio = productoServicio;
    }

    /**
     * Endpoint para cambiar el estado de aprobación de un producto (APROBADO, RECHAZADO).
     * Requiere que el usuario autenticado tenga el Rol 'MODERADOR'.
     * URL: PUT /api/moderador/productos/{id}/estado
     */
    @PutMapping("/productos/{id}/estado")
    @PreAuthorize("hasRole('MODERADOR')") // Restricción de acceso: Solo MODERADOR
    public ResponseEntity<Producto> actualizarEstadoProducto(
            @PathVariable Long id,
            @Valid @RequestBody EstadoProductoDTO dto) {

        Producto productoActualizado = productoServicio.actualizarEstadoProducto(id, dto);
        return ResponseEntity.ok(productoActualizado);
    }

    // Otros métodos útiles para el moderador (ej: listar productos pendientes)
    // @GetMapping("/productos/pendientes")
    // @PreAuthorize("hasRole('MODERADOR')")
    // ...
}
