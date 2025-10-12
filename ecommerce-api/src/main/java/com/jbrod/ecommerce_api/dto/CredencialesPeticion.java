package com.jbrod.ecommerce_api.dto;

/**
 * DTO (Data Transfer Object) para recibir las credenciales del usuario
 * durante la peticion de login.
 * Solo contiene el correo y la contrasena.
 */
public class CredencialesPeticion {

    private String correo;
    private String contrasena;

    // Constructor vacio
    public CredencialesPeticion() {
    }

    // Constructor con todos los campos
    public CredencialesPeticion(String correo, String contrasena) {
        this.correo = correo;
        this.contrasena = contrasena;
    }

    // Getters y Setters
    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}