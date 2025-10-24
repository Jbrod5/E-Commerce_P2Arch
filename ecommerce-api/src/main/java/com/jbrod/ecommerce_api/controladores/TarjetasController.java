package com.jbrod.ecommerce_api.controladores;

import com.jbrod.ecommerce_api.dto.usuario.TarjetaCreacionDto;
import com.jbrod.ecommerce_api.dto.usuario.TarjetaResponseDto;
import com.jbrod.ecommerce_api.servicios.TarjetasService;
import com.jbrod.ecommerce_api.servicios.UsuarioService;
import com.jbrod.ecommerce_api.utilidades.excepciones.RecursoNoEncontradoException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/tarjetas")
public class TarjetasController {

    private final TarjetasService tarjetasService;
    private final UsuarioService usuarioService;

    @Autowired
    public TarjetasController(TarjetasService tarjetasService, UsuarioService usuarioService) {
        this.tarjetasService = tarjetasService;
        this.usuarioService = usuarioService;
    }

    /**
     * Utilidad para obtener el ID Long del usuario del Principal (JWT/Authentication).
     */
    private Long obtenerUsuarioIdDePrincipal(Principal principal) {
        String correoUsuario = principal.getName();
        return usuarioService.obtenerUsuarioPorCorreo(correoUsuario)
                .map(u -> u.getId().longValue())
                .orElseThrow(() -> new NoSuchElementException("Usuario autenticado no encontrado en DB con correo: " + correoUsuario));
    }

    // -----------------------------------------------------------------------------------
    // GET /api/tarjetas: Obtener todas las tarjetas del usuario autenticado
    // -----------------------------------------------------------------------------------

    @GetMapping
    public ResponseEntity<List<TarjetaResponseDto>> obtenerTarjetas(Principal principal) {
        try {
            Long usuarioId = obtenerUsuarioIdDePrincipal(principal);
            List<TarjetaResponseDto> tarjetas = tarjetasService.obtenerTarjetasPorUsuario(usuarioId);
            return ResponseEntity.ok(tarjetas);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // -----------------------------------------------------------------------------------
    // POST /api/tarjetas: Agregar una nueva tarjeta al usuario autenticado
    // -----------------------------------------------------------------------------------

    @PostMapping
    public ResponseEntity<?> agregarTarjeta(
            @Valid @RequestBody TarjetaCreacionDto dto,
            Principal principal) {
        try {
            Long usuarioId = obtenerUsuarioIdDePrincipal(principal);
            TarjetaResponseDto nuevaTarjeta = tarjetasService.crearTarjeta(usuarioId, dto);
            return new ResponseEntity<>(nuevaTarjeta, HttpStatus.CREATED);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Usuario no encontrado.");
        } catch (Exception e) {
            // Puede capturar Unique Constraint Violations de la DB si el número de tarjeta ya existe
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al guardar la tarjeta: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------------------------
    // DELETE /api/tarjetas/{id}: Eliminar una tarjeta específica
    // -----------------------------------------------------------------------------------

    @DeleteMapping("/{tarjetaId}")
    public ResponseEntity<?> eliminarTarjeta(
            @PathVariable Long tarjetaId,
            Principal principal) {
        try {
            Long usuarioId = obtenerUsuarioIdDePrincipal(principal);
            tarjetasService.eliminarTarjeta(usuarioId, tarjetaId);

            // Devuelve 204 No Content para indicar éxito sin cuerpo de respuesta
            return ResponseEntity.noContent().build();

        } catch (NoSuchElementException | RecursoNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            // Error de seguridad: la tarjeta no pertenece al usuario
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage()); // 403 Forbidden
        }
    }
}