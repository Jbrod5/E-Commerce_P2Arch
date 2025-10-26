package com.jbrod.ecommerce_api.dto.solicitudes;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para recibir el comentario de rechazo de una solicitud.
 */
public class ComentarioRechazoDto {

    @NotBlank(message = "El comentario de rechazo es obligatorio.")
    @Size(min = 10, max = 500, message = "El comentario debe tener entre 10 y 500 caracteres.")
    private String comentario;

    // --- Getters y Setters ---

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
