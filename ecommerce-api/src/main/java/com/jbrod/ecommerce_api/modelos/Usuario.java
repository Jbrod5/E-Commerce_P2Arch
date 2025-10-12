package com.jbrod.ecommerce_api.modelos;


import jakarta.persistence.*;

/**
 * Entidad JPA que mapea la tabla 'usuario' de la base de datos.
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
    private String contrasena; // NOTA: Aquí se almacenará la contraseña encriptada

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellido", length = 100)
    private String apellido;

    @Column(name = "telefono", length = 20)
    private String telefono;

    // --- Relación con Rol (Muchos Usuarios tienen Un Rol) ---

    @ManyToOne // Define la relación Muchos a Uno
    @JoinColumn(name = "id_rol", nullable = false) // Especifica la columna de la llave foránea
    private Rol rol; // Un objeto Rol asociado a este Usuario

    // --- Constructores ---

    // Constructor vacio, requerido por JPA
    public Usuario() {
    }

    // Constructor completo
    public Usuario(String correo, String contrasena, String nombre, String apellido, String telefono, Rol rol) {
        this.correo = correo;
        this.contrasena = contrasena;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this
                .rol = rol;
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

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
}