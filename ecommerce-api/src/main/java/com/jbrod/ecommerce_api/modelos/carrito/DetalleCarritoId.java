package com.jbrod.ecommerce_api.modelos.carrito;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Clase para la clave compuesta del Detalle del Carrito.
 */
@Embeddable
public class DetalleCarritoId implements Serializable {

    private Long carritoId;
    private Long productoId;

    // Constructores
    public DetalleCarritoId() {

    }
    public DetalleCarritoId(Long carritoId, Long productoId) {
        this.carritoId = carritoId;
        this.productoId = productoId;
    }

    // Getters
    public Long getCarritoId() { return carritoId; }
    public Long getProductoId() { return productoId; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DetalleCarritoId that = (DetalleCarritoId) o;
        return Objects.equals(carritoId, that.carritoId) && Objects.equals(productoId, that.productoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(carritoId, productoId);
    }
}
