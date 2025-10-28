package com.jbrod.ecommerce_api.modelos.pedidos;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ListaProductoPedidoId implements Serializable {

    @Column(name = "id_pedido")
    private Long pedidoId;

    @Column(name = "id_producto")
    private Long productoId;

    // Constructores, Getters y Setters
    public ListaProductoPedidoId() {}

    public ListaProductoPedidoId(Long pedidoId, Long productoId) {
        this.pedidoId = pedidoId;
        this.productoId = productoId;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListaProductoPedidoId that = (ListaProductoPedidoId) o;
        return Objects.equals(pedidoId, that.pedidoId) && Objects.equals(productoId, that.productoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pedidoId, productoId);
    }
}