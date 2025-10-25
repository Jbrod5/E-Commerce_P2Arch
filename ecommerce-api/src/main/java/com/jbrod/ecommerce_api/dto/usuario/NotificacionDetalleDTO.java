package com.jbrod.ecommerce_api.dto.usuario;



import java.time.LocalDateTime;

/**
 * DTO utilizado para la vista de detalle de una notificación.
 * Convertido a POJO tradicional.
 */
public class NotificacionDetalleDTO {
    private Long id;
    private String titulo;
    private String cuerpo;
    private LocalDateTime fecha;
    private Boolean leida;

    // Constructor completo
    public NotificacionDetalleDTO(Long id, String titulo, String cuerpo, LocalDateTime fecha, Boolean leida) {
        this.id = id;
        this.titulo = titulo;
        this.cuerpo = cuerpo;
        this.fecha = fecha;
        this.leida = leida;
    }

    // Constructor vacío
    public NotificacionDetalleDTO() {}

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

    public String getCuerpo() {
        return cuerpo;
    }

    public void setCuerpo(String cuerpo) {
        this.cuerpo = cuerpo;
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
