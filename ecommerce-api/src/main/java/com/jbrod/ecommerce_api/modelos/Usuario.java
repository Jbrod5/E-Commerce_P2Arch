package com.jbrod.ecommerce_api.modelos;

import jakarta.persistence.*;

/**
 * Entidad JPA que mapea la tabla 'usuario' de la base de datos.
 * CORREGIDA: Eliminado 'apellido' y 'telefono', y arreglado el nombre de la columna foranea del 'Rol'.
 */
@Entity
@Table(name = "usuario")
public class Usuario {

    // --- Mapeo de Campos de la Tabla ---

    // Llave primaria
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer id;

    @Column(name = "correo", nullable = false, unique = true, length = 100)
    private String correo;

    @Column(name = "contrasena", nullable = false)
    private String contrasena;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    // **CAMPO ELIMINADO:** private String telefono;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    // --- Relación con Rol (Muchos Usuarios tienen Un Rol) ---

    @ManyToOne // Define la relación Muchos a Uno
    // CORRECCIÓN ANTERIOR: Mapeo de FK a la columna 'rol'
    @JoinColumn(name = "rol", nullable = false)
    private Rol rol; // Un objeto Rol asociado a este Usuario

    // --- Constructores ---

    // Constructor vacio, requerido por JPA
    public Usuario() {}

    // Constructor completo ACTUALIZADO (Apellido y Teléfono eliminados)
    public Usuario(String correo, String contrasena, String nombre, Boolean activo, Rol rol) {
        this.correo = correo;
        this.contrasena = contrasena;
        this.nombre = nombre;
        // Teléfono eliminado del constructor
        this.activo = activo;
        this.rol = rol;
    }

    // --- Getters y Setters ---

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // **MÉTODOS ELIMINADOS:** getTelefono() y setTelefono()

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
}