package com.jbrod.ecommerce_api.controladores;

import com.jbrod.ecommerce_api.dto.ingresos.DetalleVentaVendedorDTO;
import com.jbrod.ecommerce_api.servicios.IngresosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
// Mapea todas las peticiones a la base /api/ingresos
@RequestMapping("/api/ingresos")
// Permite la comunicación con el frontend desde cualquier origen
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class IngresosController {

    private final IngresosService ingresosService;

    // Inyección de dependencia a través del constructor
    @Autowired
    public IngresosController(IngresosService ingresosService) {
        this.ingresosService = ingresosService;
    }

    /**
     * GET /api/ingresos/vendedor/{correoVendedor}
     * Endpoint para obtener el historial de ventas detallado de un vendedor,
     * identificado por su correo electrónico (ya que este es el identificador de sesión).
     * * @param correoVendedor El correo del usuario vendedor (ej: "vendedor@test.com").
     * @return 200 OK con la lista de ventas, 204 No Content si la lista está vacía,
     * o 404 Not Found si el correo no existe.
     */
    @GetMapping("/vendedor/{correoVendedor}") // Se usa el correo en la ruta
    public ResponseEntity<?> obtenerHistorialVentasVendedor(@PathVariable("correoVendedor") String correoVendedor) {

        try {
            // El servicio resuelve el ID del usuario internamente usando el correo.
            List<DetalleVentaVendedorDTO> ventas = ingresosService.obtenerHistorialVentasVendedor(correoVendedor);

            if (ventas.isEmpty()) {
                // HTTP 204 No Content si no hay ventas
                return ResponseEntity.noContent().build();
            }

            // HTTP 200 OK con la lista de DTOs
            return ResponseEntity.ok(ventas);

        } catch (UsernameNotFoundException e) {
            // Manejo de errores si el correo no existe (HTTP 404 Not Found)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"Usuario no encontrado con el correo: " + correoVendedor + "\"}");
        } catch (Exception e) {
            // Manejo genérico de otros errores (e.g., problemas de base de datos)
            System.err.println("Error en IngresosController: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Error interno del servidor al procesar la solicitud.\"}");
        }
    }
}
