package com.jbrod.ecommerce_api.controladores;


import com.jbrod.ecommerce_api.dto.solicitudes.DecisionModeracionDto;
import com.jbrod.ecommerce_api.dto.solicitudes.SolicitudPendienteDto;
import com.jbrod.ecommerce_api.modelos.productos.Producto;
import com.jbrod.ecommerce_api.servicios.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para manejar las operaciones específicas del rol MODERADOR.
 * Requiere el prefijo /api/moderador.
 */
@RestController
@RequestMapping("/api/moderador/productos")
public class ModeradorController {

    private final ProductoService productoService;

    public ModeradorController(ProductoService productoService) {
        this.productoService = productoService;
    }

    /**
     * Obtiene el listado de todos los productos en estado 'pendiente' junto
     * con la información relevante del vendedor.
     * GET /api/moderador/productos/pendientes
     * @return Lista de DTOs de solicitudes pendientes para revisión.
     */
    @GetMapping("/pendientes")
    public ResponseEntity<List<SolicitudPendienteDto>> obtenerSolicitudesPendientes() {
        System.out.println("Se solicitaron productos pendientes");
        List<SolicitudPendienteDto> pendientes = productoService.obtenerSolicitudesPendientes();
        return ResponseEntity.ok(pendientes);
    }

    /**
     * Revisa un producto pendiente, aprobándolo o rechazándolo, y registra la acción
     * en la tabla de solicitudes.
     * POST /api/moderador/productos/{idProducto}/revisar
     * * El DTO DecisionModeracionDto debe contener:
     * - 'aprobado': boolean (true para aprobar, false para rechazar).
     * - 'comentario': string (opcional, usado para rechazo).
     * * @param idProducto ID del producto a revisar.
     * @param dto DTO con la decisión y el comentario.
     * @param authentication Datos del usuario moderador autenticado.
     * @return Producto actualizado (aprobado o rechazado).
     */
    @PostMapping("/{idProducto}/revisar")
    public ResponseEntity<Producto> revisarProducto(
            @PathVariable("idProducto") Long idProducto,
            @Valid @RequestBody DecisionModeracionDto dto,
            Authentication authentication) {

        String username = authentication.getName(); // Correo del moderador (username)

        // Llamada al servicio unificado
        Producto productoActualizado = productoService.revisarProducto(idProducto, dto, username);

        return ResponseEntity.ok(productoActualizado);
    }
}
