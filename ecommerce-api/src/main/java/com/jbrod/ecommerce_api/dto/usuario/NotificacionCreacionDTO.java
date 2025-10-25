package com.jbrod.ecommerce_api.dto.usuario;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO utilizado para la creación de una nueva notificación.
 * Convertido a POJO tradicional.
 */
public class NotificacionCreacionDTO {

    @NotNull(message = "El ID de usuario es obligatorio.")
    private Integer idUsuario;

    @NotBlank(message = "El título es obligatorio.")
    @Size(max = 200, message = "El título no puede exceder los 200 caracteres.")
    private String titulo;

    @NotBlank(message = "El cuerpo de la notificación es obligatorio.")
    private String cuerpo;

    // Constructor completo
    public NotificacionCreacionDTO(Integer idUsuario, String titulo, String cuerpo) {
        this.idUsuario = idUsuario;
        this.titulo = titulo;
        this.cuerpo = cuerpo;
    }

    // Constructor vacío
    public NotificacionCreacionDTO() {}

    // Getters y Setters

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
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
}
