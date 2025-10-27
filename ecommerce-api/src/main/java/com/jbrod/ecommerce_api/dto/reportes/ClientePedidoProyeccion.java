package com.jbrod.ecommerce_api.dto.reportes;

/**
 * Interfaz de Proyección para el reporte de Top Clientes por número de Pedidos.
 */
public interface ClientePedidoProyeccion {

    // Nombre del cliente comprador
    String getNombreCliente();

    // Total de pedidos realizados (COUNT(p.id_pedido))
    Long getTotalPedidos();
}