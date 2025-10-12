package com.jbrod.ecommerce_api.controladores;

import com.jbrod.ecommerce_api.dto.CredencialesPeticion;
import com.jbrod.ecommerce_api.servicios.JwtServicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails; // <-- Nueva Importación
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map; // <-- Nueva Importación para la respuesta JSON

/**
 * Controlador para manejar las peticiones de autenticacion (login y registro).
 * Ruta base: /api/auth
 */
@RestController
@RequestMapping("/api/auth")
public class AutenticacionControlador {

    // Inyectamos el gestor de autenticacion que configuramos en SecurityConfig
    @Autowired
    private AuthenticationManager authenticationManager;

    // INYECTAMOS EL NUEVO SERVICIO PARA MANEJAR JWT
    @Autowired
    private JwtServicio jwtServicio;

    /**
     * Endpoint para el inicio de sesion (Login).
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> autenticarUsuario(@RequestBody CredencialesPeticion credenciales) {

        try {
            // 1. Crear el token con las credenciales recibidas.
            Authentication authenticationToken =
                    new UsernamePasswordAuthenticationToken(credenciales.getCorreo(), credenciales.getContrasena());

            // 2. Intentar autenticar. Si la autenticación es exitosa, Spring nos devuelve el objeto Authentication
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            // 3. Obtener los detalles del usuario que fue cargado por UsuarioServicio
            // El principal es el objeto UserDetails que construimos
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // 4. PASO CRUCIAL: Generar el JWT
            String jwt = jwtServicio.generarToken(userDetails);

            // 5. Construir la respuesta con el Token y detalles (formato JSON)
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("token", jwt);
            respuesta.put("tipo", "Bearer"); // Standard de la industria

            // Opcional: Agregar el correo y rol para referencia del cliente
            respuesta.put("correo", userDetails.getUsername());
            respuesta.put("rol", userDetails.getAuthorities().iterator().next().getAuthority());


            // 6. Retornar el token al cliente
            return ResponseEntity.ok(respuesta);

        } catch (AuthenticationException e) {
            // 7. Manejo de Error: Si la autenticacion falla (credenciales incorrectas)
            // Aquí se retorna el ResponseEntity.status(401) sin necesidad de un Map
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Credenciales inválidas");
            errorResponse.put("mensaje", "Acceso denegado. Verifique su correo y contraseña.");

            return ResponseEntity.status(401).body(errorResponse);
        }
    }

    // NOTA: El endpoint de Registro (POST /api/auth/register) se agregará después.
}