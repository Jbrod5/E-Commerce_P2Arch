package com.jbrod.ecommerce_api.modelos.usuario;


import com.jbrod.ecommerce_api.modelos.Usuario;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad JPA que mapea la tabla 'notificacion' de la base de datos.
 */
@Entity
@Table(name = "notificacion")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    private Long id;

    // Relación Muchos a Uno con Usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario", nullable = false)
    private Usuario usuario; // Usuario al que va dirigida la notificación

    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    @Column(name = "cuerpo_de_la_notificacion", nullable = false, columnDefinition = "TEXT")
    private String cuerpo;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha = LocalDateTime.now(); // Por defecto al momento de creación


    @Column(name = "leida", nullable = false)
    private Boolean leida = false;

    // --- Constructores ---

    public Notificacion() {}

    public Notificacion(Usuario usuario, String titulo, String cuerpo) {
        this.usuario = usuario;
        this.titulo = titulo;
        this.cuerpo = cuerpo;
        // La fecha se inicializa por defecto
    }

    // --- Getters y Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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