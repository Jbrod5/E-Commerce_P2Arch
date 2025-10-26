package com.jbrod.ecommerce_api.modelos.productos;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.jbrod.ecommerce_api.modelos.Usuario;
import jakarta.persistence.*;
import java.math.BigDecimal; // IMPORTANTE: Nueva importación para manejo de decimales
import java.util.List;

/**
 * Entidad Producto: Representa un artículo puesto en venta.
 * Mapeada la tabla 'producto'.
 */
@Entity
@Table(name = "producto")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long id;

    // Relación con Usuario (id_vendedor) - CICLO ROTOOO
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vendedor", nullable = false)
    @JsonBackReference
    private Usuario vendedor;

    @Column(name = "nombre_producto", nullable = false, length = 200)
    private String nombre;

    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "imagen", nullable = true, columnDefinition = "TEXT")
    private String imagenUrl;

    // --- CAMBIOOOOO Usar BigDecimal para precio ---
    @Column(name = "precio", nullable = false, columnDefinition = "NUMERIC(12, 2)")
    private BigDecimal precio;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Column(name = "producto_nuevo", nullable = false)
    private Boolean esNuevo;

    // Relación con Categoria (categoria)
    @ManyToOne
    @JoinColumn(name = "categoria", nullable = false)
    @JsonBackReference("categoria-productos") // Rompe el ciclo Categoria <-> Producto >:c
    private Categoria categoria;

    // --- CAMBIOOOOOO 2: Usar BigDecimal para promedio_calificaciones ---
    @Column(name = "promedio_calificaciones", columnDefinition = "NUMERIC(3, 2)")
    private BigDecimal promedioCalificaciones = BigDecimal.ZERO; // Inicialización con BigDecimal

    @Column(name = "cantidad_compras")
    private Integer cantidadCompras = 0;

    // Relación con EstadoAprobacionProducto (estado_aprobacion)
    @ManyToOne
    @JoinColumn(name = "estado_aprobacion", nullable = false)
    private EstadoAprobacionProducto estado;



    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference // Maneja el ciclo de serialización si es necesario
    @JsonIgnore
    private List<SolicitudProducto> solicitudes;

    // --- Getters y Setters actualizados a BigDecimal ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getVendedor() {
        return vendedor;
    }

    public void setVendedor(Usuario vendedor) {
        this.vendedor = vendedor;
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

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    // Usamos BigDecimal para el parámetro
    public void setPrecio(BigDecimal precio) {
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

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getPromedioCalificaciones() {
        return promedioCalificaciones;
    }

    // Usamos BigDecimal para el parámetro
    public void setPromedioCalificaciones(BigDecimal promedioCalificaciones) {
        this.promedioCalificaciones = promedioCalificaciones;
    }

    public Integer getCantidadCompras() {
        return cantidadCompras;
    }

    public void setCantidadCompras(Integer cantidadCompras) {
        this.cantidadCompras = cantidadCompras;
    }

    public EstadoAprobacionProducto getEstado() {
        return estado;
    }

    public void setEstado(EstadoAprobacionProducto estado) {
        this.estado = estado;
    }


    public List<SolicitudProducto> getSolicitudes() {
        return solicitudes;
    }

    public void setSolicitudes(List<SolicitudProducto> solicitudes) {
        this.solicitudes = solicitudes;
    }
}