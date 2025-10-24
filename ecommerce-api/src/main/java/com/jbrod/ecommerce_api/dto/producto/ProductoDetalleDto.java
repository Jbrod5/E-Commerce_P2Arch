package com.jbrod.ecommerce_api.dto.producto;
import java.math.BigDecimal;
import java.util.List;

public class ProductoDetalleDto {
    private Long id;
    private String nombre;
    private String descripcion;
    private String imagenUrl;
    private BigDecimal precio;
    private Integer stock;
    private Boolean esNuevo;
    private String nombreVendedor; // De Usuario
    private String nombreCategoria; // De Categoria

    // Datos de Calificación
    private BigDecimal promedioCalificaciones; // De Producto
    private Integer cantidadResenas; // Nuevo: El tamaño de la lista de reseñas

    // Lista de Reseñas
    private List<ResenaDto> resenas;

    // Getters y Setters (solo algunos como ejemplo)
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public BigDecimal getPrecio() {
        return precio;
    }
    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
    public String getNombreVendedor() {
        return nombreVendedor;
    }
    public void setNombreVendedor(String nombreVendedor) {
        this.nombreVendedor = nombreVendedor;
    }
    public List<ResenaDto> getResenas() {
        return resenas;
    }
    public void setResenas(List<ResenaDto> resenas) {
        this.resenas = resenas;
    }
    public String getImagenUrl() {
        return imagenUrl;
    }
    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }
    public Integer getStock() {
        return stock;
    }
    public void setStock(Integer stock) {
        this.stock = stock;
    }
    public Boolean getEsNuevo() {
        return esNuevo;
    }
    public void setEsNuevo(Boolean esNuevo) {
        this.esNuevo = esNuevo;
    }
    public String getNombreCategoria() {
        return nombreCategoria;
    }
    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }
    public BigDecimal getPromedioCalificaciones() {
        return promedioCalificaciones;
    }
    public void setPromedioCalificaciones(BigDecimal promedioCalificaciones) {
        this.promedioCalificaciones = promedioCalificaciones;
    }
    public Integer getCantidadResenas() {
        return cantidadResenas;
    }
    public void setCantidadResenas(Integer cantidadResenas) {
        this.cantidadResenas = cantidadResenas;
    }
}