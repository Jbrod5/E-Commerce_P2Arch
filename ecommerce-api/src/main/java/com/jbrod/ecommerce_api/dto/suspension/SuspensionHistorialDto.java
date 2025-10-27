package com.jbrod.ecommerce_api.dto.suspension;

import com.jbrod.ecommerce_api.modelos.moderador.Suspension;

import java.time.LocalDateTime;

/**
 * DTO para representar una sanción en el historial (Reporte).
 */
public class SuspensionHistorialDto {
    private Long id;
    private String nombreUsuarioSancionado;
    private String nombreModerador;
    private String motivoSuspension;
    private LocalDateTime fechaSuspension;
    private LocalDateTime fechaFin;
    private Boolean activa;

    // Constructor que toma la entidad Suspension y extrae los datos necesarios
    public SuspensionHistorialDto(Suspension suspension) {
        this.id = suspension.getId();
        this.nombreUsuarioSancionado = suspension.getUsuarioSancionado().getNombre();
        this.nombreModerador = suspension.getModerador().getNombre();
        this.motivoSuspension = suspension.getMotivoSuspension();
        this.fechaSuspension = suspension.getFechaSuspension();
        this.fechaFin = suspension.getFechaFin();
        this.activa = suspension.getActiva();
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreUsuarioSancionado() {
        return nombreUsuarioSancionado;
    }

    public void setNombreUsuarioSancionado(String nombreUsuarioSancionado) {
        this.nombreUsuarioSancionado = nombreUsuarioSancionado;
    }

    public String getNombreModerador() {
        return nombreModerador;
    }

    public void setNombreModerador(String nombreModerador) {
        this.nombreModerador = nombreModerador;
    }

    public String getMotivoSuspension() {
        return motivoSuspension;
    }

    public void setMotivoSuspension(String motivoSuspension) {
        this.motivoSuspension = motivoSuspension;
    }

    public LocalDateTime getFechaSuspension() {
        return fechaSuspension;
    }

    public void setFechaSuspension(LocalDateTime fechaSuspension) {
        this.fechaSuspension = fechaSuspension;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Boolean getActiva() {
        return activa;
    }

    public void setActiva(Boolean activa) {
        this.activa = activa;
    }
}