package com.jbrod.ecommerce_api.controladores;

import com.jbrod.ecommerce_api.dto.CredencialesPeticion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    /**
     * Endpoint para el inicio de sesion (Login).
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> autenticarUsuario(@RequestBody CredencialesPeticion credenciales) {

        try {
            // 1. Crear el token con las credenciales recibidas.
            Authentication authenticationToken =
                    new UsernamePasswordAuthenticationToken(credenciales.getCorreo(), credenciales.getContrasena());

            // 2. Intentar autenticar. Llama internamente a nuestro UsuarioServicio.loadUserByUsername()
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            // Si la autenticacion fue exitosa, el objeto 'authentication' contiene los detalles del usuario.

            // TODO: PASO CRUCIAL: Generar el JWT aquí. Por ahora, solo indicaremos el éxito.

            // 3. Respuesta de Éxito (temporal sin JWT)
            String mensaje = "¡Autenticación exitosa para el usuario: " + authentication.getName() +
                    " con rol: " + authentication.getAuthorities().iterator().next().getAuthority() + "!";

            return ResponseEntity.ok(mensaje);

        } catch (AuthenticationException e) {
            // 4. Manejo de Error: Si la autenticacion falla (credenciales incorrectas)
            return ResponseEntity.status(401).body("Credenciales inválidas. Acceso denegado.");
        }
    }

    // NOTA: El endpoint de Registro (POST /api/auth/register) se agregará después.
}