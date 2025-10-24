package com.jbrod.ecommerce_api.modelos.pedidos;


import com.jbrod.ecommerce_api.modelos.usuario.Tarjetas; // Asumiendo que esta es la entidad para 'tarjetas'
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Long id;

    @Column(name = "usuario", nullable = false)
    private Long usuarioId; // Referencia a usuario(id_usuario)

    @Column(name = "monto_total", nullable = false, precision = 14, scale = 2)
    private BigDecimal montoTotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tarjeta_usada", referencedColumnName = "id_tarjeta", nullable = false)
    private Tarjetas tarjetaUsada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado", referencedColumnName = "id_estado_pedido", nullable = false)
    private EstadoPedido estado;

    @Column(name = "direccion", nullable = false, columnDefinition = "TEXT")
    private String direccion;

    @Column(name = "fecha_realizacion")
    private LocalDateTime fechaRealizacion; // Mapea a TIMESTAMP DEFAULT CURRENT_TIMESTAMP

    @Column(name = "fecha_entrega_estimada", nullable = false)
    private LocalDateTime fechaEntregaEstimada;

    @Column(name = "fecha_entrega_real")
    private LocalDateTime fechaEntregaReal; // NULLABLE

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ListaProductoPedido> items; // Items del pedido

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }

    public Tarjetas getTarjetaUsada() {
        return tarjetaUsada;
    }

    public void setTarjetaUsada(Tarjetas tarjetaUsada) {
        this.tarjetaUsada = tarjetaUsada;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public LocalDateTime getFechaRealizacion() {
        return fechaRealizacion;
    }

    public void setFechaRealizacion(LocalDateTime fechaRealizacion) {
        this.fechaRealizacion = fechaRealizacion;
    }

    public LocalDateTime getFechaEntregaEstimada() {
        return fechaEntregaEstimada;
    }

    public void setFechaEntregaEstimada(LocalDateTime fechaEntregaEstimada) {
        this.fechaEntregaEstimada = fechaEntregaEstimada;
    }

    public LocalDateTime getFechaEntregaReal() {
        return fechaEntregaReal;
    }

    public void setFechaEntregaReal(LocalDateTime fechaEntregaReal) {
        this.fechaEntregaReal = fechaEntregaReal;
    }

    public List<ListaProductoPedido> getItems() {
        return items;
    }

    public void setItems(List<ListaProductoPedido> items) {
        this        .items = items;
    }
}