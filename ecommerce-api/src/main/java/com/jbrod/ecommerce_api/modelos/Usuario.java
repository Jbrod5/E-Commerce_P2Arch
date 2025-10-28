package com.jbrod.ecommerce_api.modelos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.jbrod.ecommerce_api.modelos.productos.Producto;
import jakarta.persistence.*;

import java.util.List;

/**
 * Entidad JPA que mapea la tabla 'usuario' de la base de datos.
 */
@Entity
@Table(name = "usuario")
public class Usuario {



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


    @Column(name = "activo", nullable = false)
    private Boolean activo;

    // --- Relación con Rol (Muchos Usuarios tienen Un Rol) ---

    @ManyToOne // Define la relación Muchos a Uno
    // CORRECCIÓN ANTERIOR: Mapeo de FK a la columna 'rol'
    @JoinColumn(name = "rol", nullable = false)
    private Rol rol; // Un objeto Rol asociado a este Usuario

    // --- Relación con Productos (Un Usuario puede tener Muchos Productos a la venta) ---
    @OneToMany(mappedBy = "vendedor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference // Indica que esta es la referencia 'manager' o principal del ciclo bidireccional
    @JsonIgnore
    private List<Producto> productosVendidos;



    // Constructor vacio, requerido por JPA
    public Usuario() {}

    // Constructor completo
    public Usuario(String correo, String contrasena, String nombre, Boolean activo, Rol rol) {
        this.correo = correo;
        this.contrasena = contrasena;
        this.nombre = nombre;
        // Teléfono eliminado del constructor
        this.activo = activo;
        this.rol = rol;
    }



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






    public List<Producto> getProductosVendidos() {
        return productosVendidos;
    }

    public void setProductosVendidos(List<Producto> productosVendidos) {
        this.productosVendidos = productosVendidos;
    }
}