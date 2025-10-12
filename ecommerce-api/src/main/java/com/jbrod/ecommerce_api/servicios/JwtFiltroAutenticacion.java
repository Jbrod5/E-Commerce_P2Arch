package com.jbrod.ecommerce_api.servicios;


import com.jbrod.ecommerce_api.servicios.UsuarioServicio;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFiltroAutenticacion extends OncePerRequestFilter {

    @Autowired
    private JwtServicio jwtServicio;

    @Autowired
    private UsuarioServicio usuarioServicio; // Tu UserDetailsService

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Obtener el encabezado de autorización
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Si el encabezado es nulo o no comienza con "Bearer ", ignorar y pasar al siguiente filtro.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extraer el token (eliminar "Bearer ")
        jwt = authHeader.substring(7);

        // 3. Extraer el correo (username) del token
        userEmail = jwtServicio.extractUsername(jwt);

        // 4. Si encontramos el correo y el usuario no está ya autenticado en el contexto de Spring Security
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Cargar el UserDetails desde la base de datos (usando tu UsuarioServicio)
            UserDetails userDetails = this.usuarioServicio.loadUserByUsername(userEmail);

            // 5. Validar el token y la vigencia del usuario
            if (jwtServicio.isTokenValid(jwt, userDetails)) {

                // Si el token es válido, crear un objeto de autenticación
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // La contraseña es nula porque ya se verificó con el token
                        userDetails.getAuthorities()
                );

                // Agregar detalles de la petición (IP, etc.)
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 6. Establecer el usuario en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continuar con el siguiente filtro en la cadena (ya sea el controlador o el filtro de autorización)
        filterChain.doFilter(request, response);
    }
}