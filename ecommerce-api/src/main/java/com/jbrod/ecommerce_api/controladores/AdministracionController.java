package com.jbrod.ecommerce_api.controladores;

import com.jbrod.ecommerce_api.modelos.Usuario;
import com.jbrod.ecommerce_api.servicios.UsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador para la gestión de usuarios/empleados y reportes por el Administrador.
 * Ruta base: /api/admin
 */
@RestController
@RequestMapping("/api/admin")
public class AdministracionController {

    @Autowired
    private UsuarioService usuarioService;



    // --- GESTIÓN DE EMPLEADOS ----------------------------------------------------------------------------------------

    /**
     * Endpoint para registrar nuevos empleados (moderador, logistica, administrador).
     * POST /api/admin/empleados
     */
    @PostMapping("/empleados")
    public ResponseEntity<?> registrarEmpleado(@RequestBody Usuario nuevoEmpleado) {

        // Se espera que 'nuevoEmpleado' tenga el campo 'rol'
        String rolNombre = nuevoEmpleado.getRol().getNombre();

        try {
            Usuario empleadoGuardado = usuarioService.registrarEmpleado(nuevoEmpleado, rolNombre);
            empleadoGuardado.setContrasena(null); // Limpiar por seguridad

            return ResponseEntity.status(HttpStatus.CREATED).body(empleadoGuardado);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Endpoint para obtener la lista de todos los empleados (Visualizar Historial).
     * GET /api/admin/empleados
     */
    @GetMapping("/empleados")
    public ResponseEntity<?> obtenerListaEmpleados() {
        try {
            List<Usuario> empleados = usuarioService.obtenerEmpleados();

            return ResponseEntity.ok(empleados);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error al obtener la lista de empleados.", "detalle", e.getMessage()));
        }
    }

    /**
     * Endpoint para actualizar la información de un usuario/empleado.
     * PUT /api/admin/empleados/{id}
     */
    @PutMapping("/empleados/{id}")
    public ResponseEntity<?> actualizarInformacionEmpleado(@PathVariable Integer id, @RequestBody Usuario datosActualizados) {
        try {
            Usuario empleadoActualizado = usuarioService.actualizarUsuario(id, datosActualizados);
            empleadoActualizado.setContrasena(null); // Limpiar por seguridad

            return ResponseEntity.ok(empleadoActualizado);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }


}