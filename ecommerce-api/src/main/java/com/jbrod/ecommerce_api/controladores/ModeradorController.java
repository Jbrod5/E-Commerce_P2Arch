package com.jbrod.ecommerce_api.controladores;


import com.jbrod.ecommerce_api.dto.solicitudes.DecisionModeracionDto;
import com.jbrod.ecommerce_api.dto.solicitudes.SolicitudPendienteDto;
import com.jbrod.ecommerce_api.dto.suspension.SuspensionPeticionDto;
import com.jbrod.ecommerce_api.dto.usuario.UsuarioVendedorDto;
import com.jbrod.ecommerce_api.modelos.moderador.Suspension;
import com.jbrod.ecommerce_api.modelos.productos.Producto;
import com.jbrod.ecommerce_api.servicios.ModeradorService;
import com.jbrod.ecommerce_api.servicios.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Controlador REST para manejar las operaciones específicas del rol MODERADOR.
 * Requiere el prefijo /api/moderador.
 */
@RestController
@RequestMapping("/api/moderador/")
public class ModeradorController {

    private final ProductoService productoService;
    private final ModeradorService moderadorService;



    public ModeradorController(ProductoService productoService, ModeradorService moderadorService) {
        this.productoService = productoService;
        this.moderadorService = moderadorService;
    }

    /**
     * Obtiene el listado de todos los productos en estado 'pendiente' junto
     * con la información relevante del vendedor.
     * GET /api/moderador/productos/pendientes
     * @return Lista de DTOs de solicitudes pendientes para revisión.
     */
    @GetMapping("/productos/pendientes")
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
    @PostMapping("/productos/{idProducto}/revisar")
    public ResponseEntity<Producto> revisarProducto(
            @PathVariable("idProducto") Long idProducto,
            @Valid @RequestBody DecisionModeracionDto dto,
            Authentication authentication) {

        String username = authentication.getName(); // Correo del moderador (username)

        // Llamada al servicio unificado
        Producto productoActualizado = productoService.revisarProducto(idProducto, dto, username);

        return ResponseEntity.ok(productoActualizado);
    }








    /**
     * Sanciona un usuario común (vendedor) por un periodo de tiempo.
     * POST /api/moderador/sancionar
     * @param dto DTO con el ID del usuario a sancionar, motivo y fecha de fin.
     * @param authentication Datos del usuario moderador autenticado.
     * @return Objeto Suspension creado.
     */
    @PostMapping("/sancionar")
    public ResponseEntity<?> sancionarUsuario(
            @Valid @RequestBody SuspensionPeticionDto dto,
            Authentication authentication) {

        try {
            String correoModerador = authentication.getName(); // Correo del moderador

            Suspension suspensionCreada = moderadorService.sancionarUsuario(dto, correoModerador);

            return ResponseEntity.status(HttpStatus.CREATED).body(suspensionCreada);

        } catch (NoSuchElementException e) {
            // Usuario a sancionar no encontrado.
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            // Usuario no es de tipo común, o validación falló (aunque se cubre con @Valid).
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtiene la lista de usuarios con rol 'comun' (vendedores) para gestión.
     * GET /api/moderador/vendedores
     * @return Lista de DTOs de vendedores.
     */
    @GetMapping("/vendedores")
    public ResponseEntity<List<UsuarioVendedorDto>> obtenerVendedores() {
        List<UsuarioVendedorDto> vendedores = moderadorService.obtenerUsuariosVendedores();
        return ResponseEntity.ok(vendedores);
    }

}
