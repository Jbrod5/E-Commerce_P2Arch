package com.jbrod.ecommerce_api.configuraciones;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración para mapear URLs a directorios locales de archivos.
 * Esto permite que las imágenes subidas (o simuladas) sean accesibles vía HTTP.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final String RESOURCE_HANDLER = "/imagenes/**";

    // Directorio donde se guardarán temporalmente las imágenes en el entorno local (dentro del proyecto)
    // Asegúrate de que esta carpeta exista: src/main/resources/static/imagenes-mock/productos
    private static final String RESOURCE_LOCATION = "file:src/main/resources/static/imagenes/";

    /**
     * Mapea la URL base 'imagenes' a un directorio físico local.
     * Así, cuando el frontend pide http://localhost:8080/imagenes/producto.jpg,
     * Spring lo busca en el directorio /src/main/resources/static/imagenes/producto.jpg
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Habilita el manejo de recursos estáticos en la ruta /imagenes-mock/
        registry.addResourceHandler(RESOURCE_HANDLER)
                // Apunta a una ubicación física en el sistema de archivos
                .addResourceLocations(RESOURCE_LOCATION);
    }
}
