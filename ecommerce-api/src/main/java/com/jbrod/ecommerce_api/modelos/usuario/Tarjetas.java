package com.jbrod.ecommerce_api.modelos.usuario;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tarjetas")
public class Tarjetas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tarjeta")
    private Long id;

    @Column(name = "numero_tarjeta", nullable = false, unique = true, length = 50)
    private String numeroTarjeta;

    @Column(name = "parte_visible", nullable = false, length = 10)
    private String parteVisible; // Últimos 4 dígitos

    @Column(name = "titular", nullable = false, length = 200)
    private String titular;

    @Column(name = "id_usuario", nullable = false)
    private Long usuarioId; // Referencia a usuario(id_usuario)

    @Column(name = "mes_vencimiento", nullable = false)
    private Integer mesVencimiento;

    @Column(name = "anio_vencimiento", nullable = false)
    private Integer anioVencimiento;

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public String getParteVisible() {
        return parteVisible;
    }

    public void setParteVisible(String parteVisible) {
        this.parteVisible = parteVisible;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Integer getMesVencimiento() {
        return mesVencimiento;
    }

    public void setMesVencimiento(Integer mesVencimiento) {
        this.mesVencimiento = mesVencimiento;
    }

    public Integer getAnioVencimiento() {
        return anioVencimiento;
    }

    public void setAnioVencimiento(Integer anioVencimiento) {
        this.anioVencimiento = anioVencimiento;
    }
}