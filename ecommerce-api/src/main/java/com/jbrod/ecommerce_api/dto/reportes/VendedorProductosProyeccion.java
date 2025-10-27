package com.jbrod.ecommerce_api.dto.reportes;

/**
 * Interfaz de Proyección para el reporte de Top Vendedores por número de Productos a la venta.
 */
public interface VendedorProductosProyeccion {

    // Nombre del vendedor
    String getNombreVendedor();

    // Total de productos listados (COUNT(p.id_producto))
    Long getTotalProductosListados();
}