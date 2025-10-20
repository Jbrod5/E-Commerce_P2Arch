package com.jbrod.ecommerce_api.modelos.productos;

import jakarta.persistence.*;

/**
 * Entidad Categoria: Define las categorías de productos (Tecnología, Hogar, etc.).
 */
@Entity
@Table(name = "categoria_producto")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria") //
    private Long id;

    @Column(name = "nombre_categoria", nullable = false, unique = true)
    private String nombre;

    // --- Getters y Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
