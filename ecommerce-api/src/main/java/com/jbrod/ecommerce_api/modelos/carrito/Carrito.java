package com.jbrod.ecommerce_api.modelos.carrito;

import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Entidad Carrito: Representa el carrito activo de un usuario.
 */
@Entity
@Table(name = "carrito")
public class Carrito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carrito")
    private Long idCarrito; // Compatible con SERIAL

    @Column(name = "usuario", unique = true, nullable = false)
    private Long usuarioId;

    @Column(name = "fecha_creacion", nullable = false)
    private ZonedDateTime fechaCreacion = ZonedDateTime.now(); // Compatible con TIMESTAMP

    // Relación con DetalleCarrito. MappedBy apunta a la relación en DetalleCarrito.
    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleCarrito> items;

    // Constructor
    public Carrito() {}

    // Getters y Setters
    public Long getIdCarrito() { return idCarrito; }
    public void setIdCarrito(Long idCarrito) { this.idCarrito = idCarrito; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public ZonedDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(ZonedDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    // Getters y Setters para la lista de items
    public List<DetalleCarrito> getItems() { return items; }
    public void setItems(List<DetalleCarrito> items) { this.items = items; }
}
