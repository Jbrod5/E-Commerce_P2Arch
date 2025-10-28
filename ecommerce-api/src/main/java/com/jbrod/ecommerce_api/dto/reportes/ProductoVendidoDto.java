package com.jbrod.ecommerce_api.dto.reportes;


/**
 * DTO para el reporte de Top Productos Vendidos.
 * Contiene el nombre del producto y el total de unidades vendidas.
 */
public class ProductoVendidoDto {

    private String nombreProducto;
    private Long totalVendido;


    public ProductoVendidoDto(String nombreProducto, Long totalVendido) {
        this.nombreProducto = nombreProducto;
        this.totalVendido = totalVendido;
    }

    // Getters y Setters
    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public Long getTotalVendido() {
        return totalVendido;
    }

    public void setTotalVendido(Long totalVendido) {
        this.totalVendido = totalVendido;
    }
}