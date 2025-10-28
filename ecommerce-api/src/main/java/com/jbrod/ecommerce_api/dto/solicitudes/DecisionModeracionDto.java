package com.jbrod.ecommerce_api.dto.solicitudes;


public class DecisionModeracionDto {
    private boolean aprobado;
    private String comentario;

    // Constructor vacío
    public DecisionModeracionDto() {}

    // Getters y Setters
    public boolean isAprobado() {
        return aprobado;
    }

    public void setAprobado(boolean aprobado) {
        this.aprobado = aprobado;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
