package com.jbrod.ecommerce_api.modelos.carrito;

import com.jbrod.ecommerce_api.modelos.productos.Producto;
import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Entidad DetalleCarrito: Una línea de item que representa un producto y su cantidad
 * dentro de un Carrito de Compras específico.
 * Utiliza @EmbeddedId para su clave compuesta.
 */
@Entity
@Table(name = "detalle_carrito")
public class DetalleCarrito {

    // Asegúrate de que DetalleCarritoId y Carrito estén accesibles (importados o en el mismo paquete)
    // El paquete del DetalleCarritoId lo he asumido como com.jbrod.ecommerce_api.modelos.carrito;
    @EmbeddedId
    private DetalleCarritoId id;

    // 2. Relación con Carrito (Parte de la clave)
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("carritoId")
    @JoinColumn(name = "carrito")
    private Carrito carrito;

    // 3. Relación con Producto (Parte de la clave)
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productoId")
    @JoinColumn(name = "producto")
    private Producto producto;

    // 4. Atributos propios del Detalle
    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", precision = 10, scale = 2, nullable = false)
    private BigDecimal precioUnitario;

    // Constructores
    public DetalleCarrito() {
        // Asegúrate de que DetalleCarritoId se importa correctamente
        this.id = new DetalleCarritoId();
    }

    // Nota: El paquete de Carrito debe ser com.jbrod.ecommerce_api.modelos.carrito;
    public DetalleCarrito(Carrito carrito, Producto producto, Integer cantidad, BigDecimal precioUnitario) {
        this.id = new DetalleCarritoId(carrito.getIdCarrito(), producto.getId());
        this.carrito = carrito;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    // Getters y Setters

    public DetalleCarritoId getId() { return id; }
    public void setId(DetalleCarritoId id) { this.id = id; }

    public Carrito getCarrito() { return carrito; }

    public void setCarrito(Carrito carrito) {
        this.carrito = carrito;
        // Se asume que Carrito.getIdCarrito() devuelve Long
        this.id = new DetalleCarritoId(carrito.getIdCarrito(), this.id.getProductoId());
    }

    public Producto getProducto() { return producto; }

    public void setProducto(Producto producto) {
        this.producto = producto;
        // Se asume que Producto.getId() devuelve Long
        this.id = new DetalleCarritoId(this.id.getCarritoId(), producto.getId());
    }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
}
