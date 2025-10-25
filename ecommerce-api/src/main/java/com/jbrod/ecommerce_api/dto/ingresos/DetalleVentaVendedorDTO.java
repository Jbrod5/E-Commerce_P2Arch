package com.jbrod.ecommerce_api.dto.ingresos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para transferir la información de una venta específica de un vendedor al cliente.
 */
public class DetalleVentaVendedorDTO {

    private Long idDetalleVenta;
    private Long idPedido; // Referencia al pedido
    private Long idProducto; // Referencia al producto
    private String nombreProducto; // Información que tal vez necesites del Producto
    private BigDecimal gananciaVendedor;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    private LocalDateTime fecha;

    // Constructor vacío
    public DetalleVentaVendedorDTO() {}

    // Constructor con campos
    public DetalleVentaVendedorDTO(Long idDetalleVenta, Long idPedido, Long idProducto, String nombreProducto, BigDecimal gananciaVendedor, Integer cantidad, BigDecimal precioUnitario, BigDecimal subtotal, LocalDateTime fecha) {
        this.idDetalleVenta = idDetalleVenta;
        this.idPedido = idPedido;
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.gananciaVendedor = gananciaVendedor;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
        this.fecha = fecha;
    }

    // Getters y Setters
    public Long getIdDetalleVenta() {
        return idDetalleVenta;
    }

    public void setIdDetalleVenta(Long idDetalleVenta) {
        this.idDetalleVenta = idDetalleVenta;
    }

    public Long getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Long idPedido) {
        this.idPedido = idPedido;
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
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
