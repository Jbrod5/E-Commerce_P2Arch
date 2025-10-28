package com.jbrod.ecommerce_api.dto.solicitudes;


import com.jbrod.ecommerce_api.modelos.productos.Producto;
import java.math.BigDecimal;

/**
 * DTO para enviar la información de un producto pendiente a la vista del moderador.
 */
public class SolicitudPendienteDto {

    private Long idProducto;
    private String nombreProducto;
    private String descripcion;
    private String imagenUrl;
    private BigDecimal precio;
    private Boolean productoNuevo;
    private String nombreVendedor;
    private String categoria;

    // Constructor estático para mapear desde la entidad Producto
    public static SolicitudPendienteDto fromEntity(Producto producto) {
        SolicitudPendienteDto dto = new SolicitudPendienteDto();
        dto.setIdProducto(producto.getId());
        dto.setNombreProducto(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setImagenUrl(producto.getImagenUrl());
        dto.setPrecio(producto.getPrecio());
        dto.setProductoNuevo(producto.getEsNuevo());

        // Información de relaciones (asumimos que están cargadas o se cargan lazy en el servicio)
        dto.setNombreVendedor(producto.getVendedor() != null ? producto.getVendedor().getNombre() : "N/A");
        dto.setCategoria(producto.getCategoria() != null ? producto.getCategoria().getNombre() : "N/A");

        return dto;
    }

    // --- Getters y Setters ---

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Boolean getProductoNuevo() {
        return productoNuevo;
    }

    public void setProductoNuevo(Boolean productoNuevo) {
        this.productoNuevo = productoNuevo;
    }

    public String getNombreVendedor() {
        return nombreVendedor;
    }

    public void setNombreVendedor(String nombreVendedor) {
        this.nombreVendedor = nombreVendedor;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}
