package com.jbrod.ecommerce_api.controladores;


import com.jbrod.ecommerce_api.servicios.ConfiguracionGlobalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/config")
public class ConfiguracionController {

    @Autowired
    private ConfiguracionGlobalService configuracionGlobalService;

    /**
     * Actualiza la URL base del backend
     * POST /api/config/backend-url
     */
    @PostMapping("/backend-url")
    public ResponseEntity<Map<String, String>> actualizarUrlBackend(@RequestBody Map<String, String> payload) {
        String nuevaUrl = payload.get("url");

        if (nuevaUrl == null || nuevaUrl.trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "La URL no puede estar vacía");
            return ResponseEntity.badRequest().body(error);
        }

        configuracionGlobalService.setUrlBaseBackend(nuevaUrl);

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("mensaje", "URL actualizada exitosamente");
        respuesta.put("urlActual", configuracionGlobalService.getUrlBaseBackend());
        System.out.println("Esta es la nueva url del backend: " +  nuevaUrl);

        return ResponseEntity.ok(respuesta);
    }

    /**
     * Obtiene la URL base actual
     * GET /api/config/backend-url
     */
    @GetMapping("/backend-url")
    public ResponseEntity<Map<String, String>> obtenerUrlBackend() {
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("url", configuracionGlobalService.getUrlBaseBackend());
        return ResponseEntity.ok(respuesta);
    }





    // Configuracion correoooo


    @PostMapping("/correo")
    public ResponseEntity<Map<String, String>> actualizarConfiguracionCorreo(@RequestBody Map<String, String> payload) {
        String correo = payload.get("correo");
        String contrasena = payload.get("contrasena");

        if (correo == null || correo.isEmpty() || contrasena == null || contrasena.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "El correo y la contraseña no pueden estar vacíos.");
            return ResponseEntity.badRequest().body(error);
        }

        configuracionGlobalService.setCorreoRemitente(correo);
        configuracionGlobalService.setContrasenaAplicacion(contrasena);

        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Configuración de correo actualizada exitosamente.");
        respuesta.put("correoActual", correo);

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/correo")
    public ResponseEntity<Map<String, String>> obtenerConfiguracionCorreo() {
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("correo", configuracionGlobalService.getCorreoRemitente());
        respuesta.put("contrasena", configuracionGlobalService.getContrasenaAplicacion());
        return ResponseEntity.ok(respuesta);
    }
}