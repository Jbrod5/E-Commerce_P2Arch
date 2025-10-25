package com.jbrod.ecommerce_api.modelos.ingresos;

import com.jbrod.ecommerce_api.modelos.pedidos.Pedido;
import com.jbrod.ecommerce_api.modelos.productos.Producto; // Asumiendo la entidad Producto
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "detalle_venta_vendedor")
public class DetalleVentaVendedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_venta")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido", referencedColumnName = "id_pedido", nullable = false)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto", referencedColumnName = "id_producto", nullable = false)
    private Producto producto;

    @Column(name = "vendedor", nullable = false)
    private Long vendedorId; // Referencia a usuario(id_usuario)

    @Column(name = "comision_plataforma", nullable = false, precision = 14, scale = 2)
    private BigDecimal comisionPlataforma;

    @Column(name = "ganancia_vendedor", nullable = false, precision = 14, scale = 2)
    private BigDecimal gananciaVendedor;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario; // Precio congelado

    @Column(name = "subtotal", nullable = false, precision = 14, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "fecha")
    private LocalDateTime fecha; // Mapea a TIMESTAMP DEFAULT CURRENT_TIMESTAMP

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Long getVendedorId() {
        return vendedorId;
    }

    public void setVendedorId(Long vendedorId) {
        this.vendedorId = vendedorId;
    }

    public BigDecimal getComisionPlataforma() {
        return comisionPlataforma;
    }

    public void setComisionPlataforma(BigDecimal comisionPlataforma) {
        this.comisionPlataforma = comisionPlataforma;
    }

    public BigDecimal getGananciaVendedor() {
        return gananciaVendedor;
    }

    public void setGananciaVendedor(BigDecimal gananciaVendedor) {
        this.gananciaVendedor = gananciaVendedor;
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

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}