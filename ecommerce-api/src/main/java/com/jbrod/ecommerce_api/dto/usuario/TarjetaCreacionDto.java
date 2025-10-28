package com.jbrod.ecommerce_api.dto.usuario;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class TarjetaCreacionDto {

    // Simulación: en un entorno real, esto sería un token de pasarela de pago.
    @NotBlank(message = "El número de tarjeta es obligatorio.")
    @Pattern(regexp = "^[0-9]{13,16}$", message = "Número de tarjeta inválido (13 a 16 dígitos).")
    private String numeroTarjeta;

    @NotBlank(message = "El nombre del titular es obligatorio.")
    @Size(max = 200)
    private String titular;

    @NotNull(message = "El mes de vencimiento es obligatorio.")
    @Min(value = 1, message = "Mes inválido.")
    @Max(value = 12, message = "Mes inválido.")
    private Integer mesVencimiento;

    @NotNull(message = "El año de vencimiento es obligatorio.")
    // Asume que solo acepta tarjetas válidas por 10 años desde 2024
    @Min(value = 2000, message = "Año de vencimiento inválido.")
    @Max(value = 2034, message = "Año de vencimiento inválido.")
    private Integer anioVencimiento;

    // --- Getters y Setters ---

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
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