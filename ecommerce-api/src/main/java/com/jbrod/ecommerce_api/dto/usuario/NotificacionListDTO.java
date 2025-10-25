package com.jbrod.ecommerce_api.dto.usuario;

import java.time.LocalDateTime;

/**
 * DTO utilizado para la vista de lista concisa de notificaciones.
 */
public class NotificacionListDTO {
    private Long id;
    private String titulo;
    private LocalDateTime fecha;
    private Boolean leida;

    // Constructor completo
    public NotificacionListDTO(Long id, String titulo, LocalDateTime fecha, Boolean leida) {
        this.id = id;
        this.titulo = titulo;
        this.fecha = fecha;
        this.leida = leida;
    }

    // Constructor vacío
    public NotificacionListDTO() {}

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Boolean getLeida() {
        return leida;
    }

    public void setLeida(Boolean leida) {
        this.leida = leida;
    }
}
