package com.jbrod.ecommerce_api.dto.pedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PedidoResponseDto {

    private Long idPedido;
    private BigDecimal montoTotal;
    private String direccion;
    private LocalDateTime fechaEntregaEstimada;
    // Opcional: Podrías añadir detalles de los ítems o el estado, pero estos son los básicos de confirmación.

    // --- Getters y Setters ---

    public Long getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Long idPedido) {
        this.idPedido = idPedido;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public LocalDateTime getFechaEntregaEstimada() {
        return fechaEntregaEstimada;
    }

    public void setFechaEntregaEstimada(LocalDateTime fechaEntregaEstimada) {
        this.fechaEntregaEstimada = fechaEntregaEstimada;
    }
}