package com.jbrod.ecommerce_api.servicios;


import com.jbrod.ecommerce_api.dto.reportes.*;
import com.jbrod.ecommerce_api.dto.suspension.SuspensionHistorialDto;
import com.jbrod.ecommerce_api.dto.usuario.NotificacionHistorialDto;
import com.jbrod.ecommerce_api.modelos.moderador.Suspension;
import com.jbrod.ecommerce_api.modelos.usuario.Notificacion;
import com.jbrod.ecommerce_api.repositorios.ingresos.DetalleVentaVendedorRepository;
import com.jbrod.ecommerce_api.repositorios.pedido.PedidoRepository;
import com.jbrod.ecommerce_api.repositorios.productos.ProductoRepository;
import com.jbrod.ecommerce_api.repositorios.suspension.SuspensionRepository;
import com.jbrod.ecommerce_api.repositorios.usuario.NotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportesService {

    @Autowired
    private DetalleVentaVendedorRepository detalleVentaRepository;
    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private SuspensionRepository suspensionRepository;
    @Autowired
    private NotificacionRepository notificacionRepository;

    /**
     * Obtiene el Top 10 de productos más vendidos en un intervalo de tiempo.
     * @param fechaInicio Fecha de inicio (inclusiva).
     * @param fechaFin Fecha de fin (inclusiva).
     * @return Lista de ProductoVendidoDto.
     */
    public List<ProductoVendidoDto> obtenerTop10ProductosVendidos(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        System.out.println("se solicitaron top10 productos vendidos");
        // Validación básica
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin son obligatorias para el reporte.");
        }

        // El repositorio se encarga de la lógica de la consulta
        return detalleVentaRepository.findTop10ProductosMasVendidos(fechaInicio, fechaFin);
    }



    /**
     * Obtiene el Top 5 de clientes (compradores) que más monto total han gastado.
     */
    public List<ClienteGananciaProyeccion> obtenerTop5ClientesCompradores(LocalDateTime fechaInicio, LocalDateTime fechaFin) {

        System.out.println("se solicitaron los 5 clientes mas compradores");
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin son obligatorias para el reporte.");
        }

        return pedidoRepository.findTop5ClientesMasGanancias(fechaInicio, fechaFin);
    }


    /**
     * Obtiene el Top 5 de usuarios que más productos han vendido (vendedores).
     */
    public List<VendedorMasVentasProyeccion> obtenerTop5Vendedores(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        System.out.println("se solicitaron los 5 clientes mas vendedores");

        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin son obligatorias para el reporte.");
        }

        return detalleVentaRepository.findTop5VendedoresMasVentas(fechaInicio, fechaFin);
    }

    /**
     * Obtiene el Top 10 de clientes (compradores) con más pedidos.
     */
    public List<ClientePedidoProyeccion> obtenerTop10ClientesMasPedidos(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        System.out.println("se solicitaron los 10 clientes con más pedidos");

        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin son obligatorias para el reporte.");
        }

        return pedidoRepository.findTop10ClientesMasPedidos(fechaInicio, fechaFin);
    }


    /**
     * Obtiene el Top 10 de clientes (vendedores) con más productos listados.
     */
    public List<VendedorProductosProyeccion> obtenerTop10VendedoresConMasProductos() {
        System.out.println("se solicitaron los 10 vendedores con más productos listados");

        // No se requieren fechas para este reporte (a menos que se quiera filtrar por fecha de creación)
        return productoRepository.findTop10VendedoresConMasProductos();
    }


    /**
     * Obtiene el historial completo de todas las suspensiones, ordenado por la fecha más reciente.
     */
    public List<SuspensionHistorialDto> obtenerHistorialSuspensiones() {
        System.out.println("se solicitó el historial de suspensiones");

        // 1. OBTENER LA LISTA: Se usa el método con FETCH JOIN para que Hibernate cargue los nombres de Usuario.
        List<Suspension> suspensiones = suspensionRepository.findAllWithUsuariosEagerly(); // <-- CAMBIO APLICADO

        // 2. Transformar cada entidad a nuestro DTO de reporte
        return suspensiones.stream()
                .map(SuspensionHistorialDto::new)
                .collect(Collectors.toList());
    }


    /**
     * Obtiene el historial completo de todas las notificaciones, ordenado por la fecha más reciente.
     */
    public List<NotificacionHistorialDto> obtenerHistorialNotificaciones() {
        System.out.println("se solicitó el historial de notificaciones");

        // 1. Obtener la lista de entidades Notificacion usando FETCH JOIN
        List<Notificacion> notificaciones = notificacionRepository.findAllWithUsuarioEagerly();

        // 2. Transformar cada entidad a nuestro DTO de reporte
        return notificaciones.stream()
                .map(NotificacionHistorialDto::new)
                .collect(Collectors.toList());
    }
}