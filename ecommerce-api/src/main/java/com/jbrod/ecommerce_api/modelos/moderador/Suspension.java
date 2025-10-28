package com.jbrod.ecommerce_api.modelos.moderador;


import com.jbrod.ecommerce_api.modelos.Usuario;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "suspension")
public class Suspension {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_suspension")
    private Long id;

    // Usuario Sancionado
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario", nullable = false)
    private Usuario usuarioSancionado;

    // Moderador que Sanciona
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_moderador", nullable = false)
    private Usuario moderador;

    @Column(name = "motivo_suspension", nullable = false)
    private String motivoSuspension;

    @Column(name = "fecha_suspension", nullable = false)
    private LocalDateTime fechaSuspension;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDateTime fechaFin;

    @Column(name = "activa", nullable = false)
    private Boolean activa;


    public Suspension() {
        this.fechaSuspension = LocalDateTime.now();
        this.activa = true;
    }


    public Suspension(Usuario usuarioSancionado, Usuario moderador, String motivoSuspension, LocalDateTime fechaFin) {
        this.usuarioSancionado = usuarioSancionado;
        this.moderador = moderador;
        this.motivoSuspension = motivoSuspension;
        this.fechaFin = fechaFin;
        this.fechaSuspension = LocalDateTime.now();
        this.activa = true;
    }

    // --- Getters y Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuarioSancionado() {
        return usuarioSancionado;
    }

    public void setUsuarioSancionado(Usuario usuarioSancionado) {
        this.usuarioSancionado = usuarioSancionado;
    }

    public Usuario getModerador() {
        return moderador;
    }

    public void setModerador(Usuario moderador) {
        this.moderador = moderador;
    }

    public String getMotivoSuspension() {
        return motivoSuspension;
    }

    public void setMotivoSuspension(String motivoSuspension) {
        this.motivoSuspension = motivoSuspension;
    }

    public LocalDateTime getFechaSuspension() {
        return fechaSuspension;
    }

    public void setFechaSuspension(LocalDateTime fechaSuspension) {
        this.fechaSuspension = fechaSuspension;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Boolean getActiva() {
        return activa;
    }

    public void setActiva(Boolean activa) {
        this.activa = activa;
    }
}