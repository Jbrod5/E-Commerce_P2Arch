package com.jbrod.ecommerce_api.modelos.productos;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CalificacionProductoId implements Serializable {

    private static final long serialVersionUID = 1L;

    // Mapea la columna 'producto' (FK a producto)
    private Long producto;

    // Mapea la columna 'usuario' (FK a usuario).
    private Integer usuario;

    public CalificacionProductoId() {}

    public CalificacionProductoId(Long producto, Integer usuario) {
        this.producto = producto;
        this.usuario = usuario;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CalificacionProductoId that = (CalificacionProductoId) o;
        return Objects.equals(producto, that.producto) && Objects.equals(usuario, that.usuario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(producto, usuario);
    }

    public Long getProducto() {
        return producto;
    }

    public void setProducto(Long producto) {
        this.producto = producto;
    }

    public Integer getUsuario() {
        return usuario;
    }

    public void setUsuario(Integer usuario) {
        this.usuario = usuario;
    }
}