package com.jbrod.ecommerce_api.dto.reportes;

public interface VendedorMasVentasProyeccion {

    // Nombre del vendedor (del JOIN con la tabla usuario)
    String getNombreVendedor();

    // Total de unidades vendidas (SUM(dvv.cantidad))
    Long getTotalUnidadesVendidas();
}