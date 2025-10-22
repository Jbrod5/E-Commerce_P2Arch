package com.jbrod.ecommerce_api.dto.carrito;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * DTO principal para la respuesta del carrito, incluyendo el total calculado.
 */
public class CarritoResponseDto {

    private Long idCarrito;
    private Long usuarioId;
    private ZonedDateTime fechaCreacion;
    private List<DetalleCarritoResponseDto> items;
    private BigDecimal montoTotal; // El total de todos los subtotales

    // Getters y Setters

    public Long getIdCarrito() { return idCarrito; }
    public void setIdCarrito(Long idCarrito) { this.idCarrito = idCarrito; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public ZonedDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(ZonedDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public List<DetalleCarritoResponseDto> getItems() { return items; }
    public void setItems(List<DetalleCarritoResponseDto> items) { this.items = items; }

    public BigDecimal getMontoTotal() { return montoTotal; }
    public void setMontoTotal(BigDecimal montoTotal) { this.montoTotal = montoTotal; }
}
