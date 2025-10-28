package com.jbrod.ecommerce_api.controladores;

import com.jbrod.ecommerce_api.dto.CredencialesPeticion;
import com.jbrod.ecommerce_api.servicios.JwtService;
import com.jbrod.ecommerce_api.servicios.UsuarioService; // <-- NUEVO: Para manejar el registro
import com.jbrod.ecommerce_api.modelos.Usuario; // <-- NUEVO: Para recibir el objeto de registro

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // <-- NUEVO: Para el estado 201 CREATED
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para manejar las peticiones de autenticacion (login y registro).
 * Ruta base: /api/auth
 */
@RestController
@RequestMapping("/api/auth")
public class AutenticacionController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UsuarioService usuarioService;

    // --- ENDPOINT DE LOGIN -------------------------------------------------------------------------------------------


    /**
     * Endpoint para el inicio de sesion (Login).
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> autenticarUsuario(@RequestBody CredencialesPeticion credenciales) {

        try {
            // 1. Crear el token con las credenciales recibidas
            Authentication authenticationToken =
                    new UsernamePasswordAuthenticationToken(credenciales.getCorreo(), credenciales.getContrasena());

            // 2. Intentar autenticar
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            // 3. Obtener los detalles del usuario
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // 4. Generar el JWT
            String jwt = jwtService.generarToken(userDetails);

            // 5. Construir la respuesta con el Token y detalles
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("token", jwt);
            respuesta.put("tipo", "Bearer");

            // Agregar info util como el correo y rol
            respuesta.put("correo", userDetails.getUsername());
            respuesta.put("rol", userDetails.getAuthorities().iterator().next().getAuthority());
            respuesta.put("nombre", usuarioService.obtenerPorCorreo(userDetails.getUsername()).getNombre());


            // 6. Retornar el token al cliente
            return ResponseEntity.ok(respuesta);

        } catch (AuthenticationException e) {
            // 7. Manejo de Error de autenticacion
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Credenciales inválidas");
            errorResponse.put("mensaje", "Acceso denegado. Verifique su correo y contraseña.");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }


    // --- ENDPOINT DE REGISTRO ----------------------------------------------------------------------------------------


    /**
     * Endpoint para el registro de nuevos usuarios.
     * POST /api/auth/register
     * * @param nuevoUsuario El objeto Usuario (con nombre, correo y contraseña).
     * @return ResponseEntity con el Usuario creado (sin contraseña) y estado 201 CREATED.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario nuevoUsuario) {

        try {
            // Llama al servicio para codificar la contraseña, asignar rol y guardar
            Usuario usuarioGuardado = usuarioService.registrarNuevoUsuario(nuevoUsuario);

            // Limpiar la contraseña antes de devolver el objeto
            usuarioGuardado.setContrasena(null);

            // Retorna un estado 201 CREATED con el objeto del usuario creado :3
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioGuardado);

        } catch (RuntimeException e) {
            // Maneja la excepción si el correo ya existe o si el rol no se encuentra
            // Retorna un estado 400 BAD REQUEST con el mensaje de error
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}