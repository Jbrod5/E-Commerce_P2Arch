package com.jbrod.ecommerce_api.dto.pedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PedidoDetalleDto {
    private Long idPedido;
    private BigDecimal montoTotal;

    // Estado y Fechas
    private String estadoNombre;
    private LocalDateTime fechaRealizacion;
    private LocalDateTime fechaEntregaEstimada;
    private LocalDateTime fechaEntregaReal; // Puede ser null

    // Dirección y Tarjeta
    private String direccion;
    private String tarjetaParteVisible; // Necesitas un JOIN a Tarjetas para obtener esto

    // Lista de Productos (Items)
    private List<ItemPedidoDto> items;

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEstadoNombre() {
        return estadoNombre;
    }

    public void setEstadoNombre(String estadoNombre) {
        this.estadoNombre = estadoNombre;
    }

    public LocalDateTime getFechaEntregaEstimada() {
        return fechaEntregaEstimada;
    }

    public void setFechaEntregaEstimada(LocalDateTime fechaEntregaEstimada) {
        this.fechaEntregaEstimada = fechaEntregaEstimada;
    }

    public LocalDateTime getFechaEntregaReal() {
        return fechaEntregaReal;
    }

    public void setFechaEntregaReal(LocalDateTime fechaEntregaReal) {
        this.fechaEntregaReal = fechaEntregaReal;
    }

    public LocalDateTime getFechaRealizacion() {
        return fechaRealizacion;
    }

    public void setFechaRealizacion(LocalDateTime fechaRealizacion) {
        this.fechaRealizacion = fechaRealizacion;
    }

    public Long getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Long idPedido) {
        this.idPedido = idPedido;
    }

    public List<ItemPedidoDto> getItems() {
        return items;
    }

    public void setItems(List<ItemPedidoDto> items) {
        this.items = items;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }

    public String getTarjetaParteVisible() {
        return tarjetaParteVisible;
    }

    public void setTarjetaParteVisible(String tarjetaParteVisible) {
        this.tarjetaParteVisible = tarjetaParteVisible;
    }
}