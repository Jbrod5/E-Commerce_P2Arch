package com.jbrod.ecommerce_api.dto.pedido;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CheckoutRequestDto {

    @NotNull(message = "Debe seleccionar una tarjeta para el pago.")
    private Long tarjetaId;

    @NotBlank(message = "La dirección de envío es obligatoria.")
    private String direccion;


    public Long getTarjetaId() {
        return tarjetaId;
    }

    public void setTarjetaId(Long tarjetaId) {
        this.tarjetaId = tarjetaId;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}