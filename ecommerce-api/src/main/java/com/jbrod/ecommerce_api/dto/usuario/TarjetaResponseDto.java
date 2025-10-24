package com.jbrod.ecommerce_api.dto.usuario;

import com.jbrod.ecommerce_api.modelos.usuario.Tarjetas;

public class TarjetaResponseDto {

    private Long id;
    private String parteVisible; // Los últimos 4 dígitos
    private String titular;
    private Integer mesVencimiento;
    private Integer anioVencimiento;

    // Opcional: Tipo de tarjeta (Visa, Mastercard, etc.)

    // Constructor de mapeo (conveniente para el servicio)
    public TarjetaResponseDto(Tarjetas tarjeta) {
        this.id = tarjeta.getId();
        this.parteVisible = tarjeta.getParteVisible();
        this.titular = tarjeta.getTitular();
        this.mesVencimiento = tarjeta.getMesVencimiento();
        this.anioVencimiento = tarjeta.getAnioVencimiento();
    }

    // Constructor vacío (si se necesita)
    public TarjetaResponseDto() {
    }

    // --- Getters y Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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