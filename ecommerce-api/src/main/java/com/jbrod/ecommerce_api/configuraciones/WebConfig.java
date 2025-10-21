package com.jbrod.ecommerce_api.configuraciones;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración para mapear URLs a directorios de archivos dentro del proyecto (classpath).
 * Esto permite que las imágenes subidas sean accesibles vía HTTP.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // La URL lógica que el navegador va a solicitar, ej: http://localhost:8080/imagenes/productos/imagen.png
    private static final String RESOURCE_HANDLER = "/imagenes/**";

    // Ubicación física de las imágenes dentro de la estructura de recursos del proyecto (classpath).
    // Esto se mapea a la carpeta 'src/main/resources/static/imagenes/'
    private static final String RESOURCE_LOCATION = "classpath:/static/imagenes/";

    /**
     * Mapea la URL base 'imagenes' a un directorio físico local usando la ruta de classpath.
     * Así, cuando el frontend pide http://localhost:8080/imagenes/producto.jpg,
     * Spring lo busca en el directorio classpath:/static/imagenes/producto.jpg
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Habilita el manejo de recursos estáticos en la ruta /imagenes/
        registry.addResourceHandler(RESOURCE_HANDLER)
                // Apunta a una ubicación dentro del classpath (la forma más robusta)
                .addResourceLocations(RESOURCE_LOCATION);
    }
}
