package com.jbrod.ecommerce_api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO utilizado para solicitar un cambio de estado de un producto (APROBADO, RECHAZADO, etc.).
 */
public class EstadoProductoDTO {

    // El nombre del estado destino (ej: "aprobado", "rechazado").
    @NotBlank(message = "El nombre del estado es obligatorio.")
    private String nombreEstado;

    // Opcional: Razón por la que se rechaza o sanciona.
    private String razon;

    // --- Constructor vacío y completo ---
    public EstadoProductoDTO() {}

    public EstadoProductoDTO(String nombreEstado, String razon) {
        this.nombreEstado = nombreEstado;
        this.razon = razon;
    }

    // --- Getters y Setters ---

    public String getNombreEstado() {
        return nombreEstado;
    }

    public void setNombreEstado(String nombreEstado) {
        this.nombreEstado = nombreEstado;
    }

    public String getRazon() {
        return razon;
    }

    public void setRazon(String razon) {
        this.razon = razon;
    }
}
