package com.jbrod.ecommerce_api.dto.usuario;


import com.jbrod.ecommerce_api.modelos.Usuario;

/**
 * DTO para enviar la información relevante de un vendedor (usuario común) al frontend,
 * sin exponer la contraseña ni otros datos sensibles.
 */
public class UsuarioVendedorDto {
    private Integer id;
    private String nombre;
    private String correo;
    private Boolean activo;
    private String rolNombre;

    public UsuarioVendedorDto() {}

    // Constructor de mapeo (conveniente para el servicio)
    public UsuarioVendedorDto(Usuario usuario) {
        this.id = usuario.getId();
        this.nombre = usuario.getNombre();
        this.correo = usuario.getCorreo();
        this.activo = usuario.getActivo();
        this.rolNombre = usuario.getRol() != null ? usuario.getRol().getNombre() : "N/A";
    }

    // --- Getters y Setters ---

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getRolNombre() {
        return rolNombre;
    }

    public void setRolNombre(String rolNombre) {
        this.rolNombre = rolNombre;
    }
}