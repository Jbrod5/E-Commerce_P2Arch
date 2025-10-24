package com.jbrod.ecommerce_api.modelos.pedidos;

import com.jbrod.ecommerce_api.modelos.productos.Producto; // Asumiendo la entidad Producto
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "lista_producto_pedido")
public class ListaProductoPedido {

    @EmbeddedId
    private ListaProductoPedidoId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("pedidoId")
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productoId")
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario; // Precio congelado

    @Column(name = "subtotal", nullable = false, precision = 14, scale = 2)
    private BigDecimal subtotal;

    // Getters y Setters
    public ListaProductoPedidoId getId() {
        return id;
    }

    public void setId(ListaProductoPedidoId id) {
        this.id = id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
        if (this.id == null) {
            this.id = new ListaProductoPedidoId();
        }
        this.id.setPedidoId(pedido.getId());
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
        if (this.id == null) {
            this.id = new ListaProductoPedidoId();
        }
        this.id.setProductoId(producto.getId());
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}