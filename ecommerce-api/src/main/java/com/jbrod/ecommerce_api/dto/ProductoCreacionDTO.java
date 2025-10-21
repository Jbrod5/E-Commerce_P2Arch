package com.jbrod.ecommerce_api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO utilizado para recibir los datos de un nuevo producto de un vendedor.
 */
public class ProductoCreacionDTO {

    @NotBlank(message = "El nombre del producto es obligatorio.")
    private String nombre;

    @NotBlank(message = "La descripción del producto es obligatoria.")
    private String descripcion;

    // Ya no se usa para recibir, pero lo dejo por si es parte de tu modelo
    private String imagenUrl;

    @NotNull(message = "El precio es obligatorio.")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0.")
    private Double precio;

    @NotNull(message = "El stock es obligatorio.")
    @Min(value = 1, message = "El stock mínimo debe ser 1.")
    private Integer stock;

    @NotNull(message = "Debe indicar si el producto es nuevo o usado.")
    private Boolean esNuevo;

    @NotNull(message = "La categoría es obligatoria.")
    private Long idCategoria; // Solo recibimos el ID de la categoría

    // *************************************************************
    // CAMBIO CLAVE: Nuevo campo para recibir la imagen Base64
    // *************************************************************
    @NotBlank(message = "La imagen en formato Base64 es obligatoria.")
    private String imagenBase64;

    // --- Getters y Setters ---

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

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
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

    public Long getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Long idCategoria) {
        this.idCategoria = idCategoria;
    }

    // *************************************************************
    // Getter y Setter para Base64
    // *************************************************************
    public String getImagenBase64() {
        return imagenBase64;
    }

    public void setImagenBase64(String imagenBase64) {
        this.imagenBase64 = imagenBase64;
    }
}
