package com.jbrod.ecommerce_api.modelos.productos;


import com.jbrod.ecommerce_api.modelos.Usuario;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "calificacion_producto")
public class CalificacionProducto {

    // Clave primaria compuesta (Embeddable)
    @EmbeddedId
    private CalificacionProductoId id;

    // Relación ManyToOne con Producto
    // Mapea la columna 'producto' de la clave compuesta
    @ManyToOne(fetch = FetchType.EAGER) // EAGER para facilitar el mapeo en el Service
    @MapsId("producto")
    @JoinColumn(name = "producto", nullable = false)
    private Producto producto;

    // Relación ManyToOne con Usuario que reseña
    // Mapea la columna 'usuario' de la clave compuesta
    @ManyToOne(fetch = FetchType.EAGER) // EAGER para obtener el nombre del reseñista
    @MapsId("usuario")
    @JoinColumn(name = "usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "calificacion", nullable = false)
    private Integer calificacion; // Entero de 1 a 5

    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario; // Puede ser NULL

    @Column(name = "fecha", columnDefinition = "TIMESTAMP")
    private LocalDateTime fecha;

    // --- Constructor, Getters y Setters ---

    public CalificacionProducto() {
        this.fecha = LocalDateTime.now();
    }

    // Asumo que tienes el resto de Getters y Setters...

    public CalificacionProductoId getId() {
        return id;
    }

    public void setId(CalificacionProductoId id) {
        this.id = id;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Integer getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Integer calificacion) {
        this.calificacion = calificacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}