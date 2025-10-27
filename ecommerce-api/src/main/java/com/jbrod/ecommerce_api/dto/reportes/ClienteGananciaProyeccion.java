package com.jbrod.ecommerce_api.dto.reportes;


import java.math.BigDecimal;

/**
 * Interfaz de Proyección para el reporte de Top Clientes Compradores.
 */
public interface ClienteGananciaProyeccion {

    // Nombre del cliente comprador (del JOIN con la tabla usuario)
    String getNombreCliente();

    // Suma de todos los pedidos realizados por el cliente
    BigDecimal getMontoTotalCompras();

    // Suma de la comisión (5% del monto total) generada para la plataforma
    BigDecimal getGananciaPlataforma();
}