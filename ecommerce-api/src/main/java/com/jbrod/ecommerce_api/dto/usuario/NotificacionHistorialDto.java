package com.jbrod.ecommerce_api.dto.usuario;


import com.jbrod.ecommerce_api.modelos.usuario.Notificacion;
import java.time.LocalDateTime;

/**
 * DTO para representar una Notificación en el historial (Reporte).
 */
public class NotificacionHistorialDto {
    private Long id;
    private String nombreUsuarioDestino; // Nombre del usuario al que va dirigida
    private String titulo;
    private String cuerpo;
    private LocalDateTime fecha;
    private Boolean leida;

    // Constructor que toma la entidad Notificacion y extrae los datos necesarios
    public NotificacionHistorialDto(Notificacion notificacion) {
        this.id = notificacion.getId();
        // NOTA: Asumimos que la entidad Usuario ya fue cargada EAGERLY con FETCH JOIN
        this.nombreUsuarioDestino = notificacion.getUsuario().getNombre();
        this.titulo = notificacion.getTitulo();
        this.cuerpo = notificacion.getCuerpo();
        this.fecha = notificacion.getFecha();
        this.leida = notificacion.getLeida();
    }

    // --- Getters y Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreUsuarioDestino() {
        return nombreUsuarioDestino;
    }

    public void setNombreUsuarioDestino(String nombreUsuarioDestino) {
        this.nombreUsuarioDestino = nombreUsuarioDestino;
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