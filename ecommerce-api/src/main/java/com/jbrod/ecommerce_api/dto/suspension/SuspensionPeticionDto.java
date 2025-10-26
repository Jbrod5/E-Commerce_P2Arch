package com.jbrod.ecommerce_api.dto.suspension;


import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class SuspensionPeticionDto {

    @NotNull(message = "El ID del usuario a suspender es obligatorio.")
    private Integer idUsuarioASuspender;

    @NotBlank(message = "El motivo de la suspensión es obligatorio.")
    private String motivo;

    @NotNull(message = "La fecha de finalización de la sanción es obligatoria.")
    @Future(message = "La fecha de fin debe ser posterior a la fecha actual.")
    private LocalDateTime fechaFin;

    // Constructores, Getters y Setters...

    public Integer getIdUsuarioASuspender() {
        return idUsuarioASuspender;
    }

    public void setIdUsuarioASuspender(Integer idUsuarioASuspender) {
        this.idUsuarioASuspender = idUsuarioASuspender;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }
}