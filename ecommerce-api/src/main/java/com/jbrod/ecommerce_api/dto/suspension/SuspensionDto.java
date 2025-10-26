package com.jbrod.ecommerce_api.dto.suspension;

import com.jbrod.ecommerce_api.modelos.moderador.Suspension;

import java.time.LocalDateTime;

public class SuspensionDTO {
    private Long id;
    private String motivo;
    private LocalDateTime fechaSuspension;
    private LocalDateTime fechaFin;
    private String nombreModerador; // Para saber quién la aplicó
    private boolean activa;

    // Constructor que acepta la entidad Suspension y mapea los campos
    public SuspensionDTO(Suspension suspension) {
        this.id = suspension.getId();
        this.motivo = suspension.getMotivoSuspension();
        this.fechaSuspension = suspension.getFechaSuspension();
        this.fechaFin = suspension.getFechaFin();
        this.activa = suspension.isActiva();

        // Mapeo seguro del moderador
        if (suspension.getModerador() != null) {
            this.nombreModerador = suspension.getModerador().getNombre();
        } else {
            this.nombreModerador = "Moderador Eliminado";
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
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

    public String getNombreModerador() {
        return nombreModerador;
    }

    public void setNombreModerador(String nombreModerador) {
        this.nombreModerador = nombreModerador;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }
}