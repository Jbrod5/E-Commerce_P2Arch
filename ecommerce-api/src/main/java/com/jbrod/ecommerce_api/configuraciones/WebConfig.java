package com.jbrod.ecommerce_api.configuraciones;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración para mapear URLs a directorios de archivos dentro del proyecto (classpath).
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final String RESOURCE_HANDLER = "/imagenes/**";
    private static final String RESOURCE_LOCATION = "classpath:/static/imagenes/";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/imagenes/**")
                .addResourceLocations("file:uploads/");
    }
}