package com.jbrod.ecommerce_api.servicios;


import org.springframework.stereotype.Service;

@Service
public class ConfiguracionGlobalService {

    // URL base por defecto
    private String urlBaseBackend = "http://localhost:8080";

    //correo
    private String correoRemitente = "";
    private String contrasenaAplicacion = "";



    /**
     * Obtiene la URL base actual del backend
     */
    public String getUrlBaseBackend() {
        return urlBaseBackend;
    }

    /**
     * Actualiza la URL base del backend
     */
    public void setUrlBaseBackend(String nuevaUrl) {
        // Limpiar la URL (quitar / al final si existe)
        if (nuevaUrl.endsWith("/")) {
            nuevaUrl = nuevaUrl.substring(0, nuevaUrl.length() - 1);
        }
        this.urlBaseBackend = nuevaUrl;
    }

    /**
     * Convierte una URL de imagen guardada en BD a la URL actual del backend
     */
    public String convertirUrlImagen(String urlImagenBD) {
        System.out.println("Se convertira esta url de imagen: " + urlImagenBD);
        if (urlImagenBD == null || urlImagenBD.isEmpty()) {
            return urlImagenBD;
        }

        // Si la URL ya es relativa, agregarle la base actual
        if (urlImagenBD.startsWith("/imagenes/")) {
            System.out.println("La imagen resultante es: " + urlBaseBackend + urlImagenBD);
            return urlBaseBackend + urlImagenBD;
        }

        // Si tiene localhost u otra URL anterior configurada, reemplazarla :3
        // Buscar /imagenes/ y tomar todo desde ahí
        int indiceImagenes = urlImagenBD.indexOf("/imagenes/");
        if (indiceImagenes != -1) {
            String rutaRelativa = urlImagenBD.substring(indiceImagenes);
            System.out.println("La imagen resultante es: " + urlBaseBackend + rutaRelativa);
            return urlBaseBackend + rutaRelativa;
        }

        // Si no tiene ningún patrón conocido, devolverla tal cual
        System.out.println("se convirtio la url de una imagen por esta ruta: " + urlImagenBD);
        return urlImagenBD;
    }



    // CORREOOOO

    public String getCorreoRemitente() {
        return correoRemitente;
    }

    public void setCorreoRemitente(String correoRemitente) {
        this.correoRemitente = correoRemitente;
    }

    public String getContrasenaAplicacion() {
        return contrasenaAplicacion;
    }

    public void setContrasenaAplicacion(String contrasenaAplicacion) {
        this.contrasenaAplicacion = contrasenaAplicacion;
    }
}