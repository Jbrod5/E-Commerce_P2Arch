package com.jbrod.ecommerce_api.modelos.productos;

import jakarta.persistence.*;

/**
 * Entidad EstadoAprobacionProducto: Mapea a la tabla de referencia
 * estado_aprobacion_producto.
 * (Ejemplo: 1=pendiente, 2=aprobado, 3=rechazado).
 */
@Entity
@Table(name = "estado_aprobacion_producto")
public class EstadoAprobacionProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado_aprobacion")
    private Integer id;

    @Column(name = "nombre_estado_aprobacion", nullable = false, unique = true, length = 50) // Mapea a VARCHAR(50)
    private String nombre;

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
}