package com.jbrod.ecommerce_api.controladores;


import com.jbrod.ecommerce_api.dto.usuario.NotificacionDetalleDTO;
import com.jbrod.ecommerce_api.dto.usuario.NotificacionListDTO;
import com.jbrod.ecommerce_api.servicios.NotificacionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    /**
     * Obtiene una lista concisa de todas las notificaciones del usuario autenticado.
     * URL: GET /api/notificaciones
     * @param authentication Objeto de autenticación para obtener el correo del usuario.
     * @return Lista de NotificacionListDTO.
     */
    @GetMapping
    public ResponseEntity<List<NotificacionListDTO>> obtenerTodasLasNotificaciones(Authentication authentication) {
        String correoUsuario = authentication.getName();
        try {
            List<NotificacionListDTO> notificaciones = notificacionService.obtenerNotificacionesPorUsuario(correoUsuario);
            return ResponseEntity.ok(notificaciones);
        } catch (NoSuchElementException e) {
            // Usuario autenticado no encontrado (casi imposible con Spring Security bien configurado)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Obtiene el número de notificaciones no leídas (para el icono de campana).
     * URL: GET /api/notificaciones/no-leidas/count
     * @param authentication Objeto de autenticación.
     * @return El número de notificaciones no leídas.
     */
    @GetMapping("/no-leidas/count")
    public ResponseEntity<Long> contarNotificacionesNoLeidas(Authentication authentication) {
        String correoUsuario = authentication.getName();
        try {
            long count = notificacionService.contarNoLeidas(correoUsuario);
            return ResponseEntity.ok(count);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(0L);
        }
    }

    /**
     * Obtiene el detalle de una notificación y la marca como leída.
     * URL: GET /api/notificaciones/{id}
     * @param id ID de la notificación.
     * @param authentication Objeto de autenticación.
     * @return NotificacionDetalleDTO.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerDetalleYMarcarLeida(@PathVariable("id") Long id, Authentication authentication) {
        String correoUsuario = authentication.getName();
        try {
            NotificacionDetalleDTO detalle = notificacionService.obtenerYMarcarComoLeida(id, correoUsuario);
            return ResponseEntity.ok(detalle);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (SecurityException e) {
            // Si la notificación no pertenece al usuario autenticado (403 Forbidden)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}