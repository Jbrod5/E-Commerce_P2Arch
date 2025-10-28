package com.jbrod.ecommerce_api.servicios;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFiltroAutenticacion extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioService usuarioService;

    public JwtFiltroAutenticacion(JwtService jwtService, UsuarioService usuarioService) {
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
    }

    /**
     * Intercepta la solicitud HTTP para manejar la autenticación con un Token Web JSON (JWT).
     * Busca el JWT en el encabezado 'Authorization'.
     * Si lo encuentra y es valido, carga los datos del usuario.
     * Establece la identidad del usuario en el contexto de seguridad de Spring Security,
     * permitiendo que la solicitud continúe con los permisos de ese usuario.
     * Si no hay token o no es válido, permite que la cadena de filtros continúe sin autenticar al usuario.
     *
     * @param request La solicitud HTTP.
     * @param response La respuesta HTTP.
     * @param filterChain La cadena de filtros para continuar el proceso.
     * @throws jakarta.servlet.ServletException Si ocurre un error interno del servidor.
     * @throws java.io.IOException Si ocurre un error de entrada/salida.
     */
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
        userEmail = jwtService.extractUsername(jwt);

        // 4. Si encontramos el correo y el usuario no está ya autenticado en el contexto de Spring Security
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Cargar el UserDetails desde la base de datos (usando tu UsuarioServicio)
            UserDetails userDetails = this.usuarioService.loadUserByUsername(userEmail);

            // 5. Validar el token y la vigencia del usuario
            if (jwtService.isTokenValid(jwt, userDetails)) {

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