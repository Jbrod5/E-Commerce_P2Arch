package com.jbrod.ecommerce_api.modelos.productos;


import com.jbrod.ecommerce_api.modelos.Usuario;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad SolicitudProducto: Registra el proceso de aprobación de un producto
 * por parte de un moderador.
 * Mapeada a la tabla 'solicitud_producto'.
 */
@Entity
@Table(name = "solicitud_producto")
public class SolicitudProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud")
    private Long id;

    // Relación con Producto (el producto que se está solicitando)
    @OneToOne
    @JoinColumn(name = "producto", nullable = false)
    private Producto producto;

    // Relación con Moderador (puede ser NULL hasta que se revise)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moderador", nullable = true) // FK a usuario(id_usuario)
    private Usuario moderador;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud = LocalDateTime.now();

    @Column(name = "fecha_revision") // Puede ser NULL
    private LocalDateTime fechaRevision;

    @Column(name = "aprobado") // Puede ser NULL hasta la revisión
    private Boolean aprobado;

    @Column(name = "comentario_moderador", columnDefinition = "TEXT") // Puede ser NULL
    private String comentarioModerador;

    // --- Getters y Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Usuario getModerador() {
        return moderador;
    }

    public void setModerador(Usuario moderador) {
        this.moderador = moderador;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDateTime fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public LocalDateTime getFechaRevision() {
        return fechaRevision;
    }

    public void setFechaRevision(LocalDateTime fechaRevision) {
        this.fechaRevision = fechaRevision;
    }

    public Boolean getAprobado() {
        return aprobado;
    }

    public void setAprobado(Boolean aprobado) {
        this.aprobado = aprobado;
    }

    public String getComentarioModerador() {
        return comentarioModerador;
    }

    public void setComentarioModerador(String comentarioModerador) {
        this.comentarioModerador = comentarioModerador;
    }
}
