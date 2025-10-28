package com.jbrod.ecommerce_api.configuraciones;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filtro que agrega headers necesarios para que ngrok sirva imágenes correctamente.
 * Los filtros SÍ funcionan con recursos estáticos, a diferencia de los interceptores.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // Se ejecuta primero
public class NgrokImagenesFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();

        // Solo agregar headers si la petición es para imagenes
        if (path.startsWith("/imagenes/")) {
            // Header para ngrok
            httpResponse.setHeader("ngrok-skip-browser-warning", "true");

            // Headers CORS para imágenes
            httpResponse.setHeader("Access-Control-Allow-Origin", "*");
            httpResponse.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
            httpResponse.setHeader("Access-Control-Allow-Headers", "*");

            // Si es OPTIONS (preflight), responder inmediatamente
            if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
                httpResponse.setStatus(HttpServletResponse.SC_OK);
                return;
            }
        }

        // Continuar con la cadena de filtros
        chain.doFilter(request, response);
    }
}