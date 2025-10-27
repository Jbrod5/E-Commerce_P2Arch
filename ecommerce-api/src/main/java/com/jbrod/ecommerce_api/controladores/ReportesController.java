package com.jbrod.ecommerce_api.controladores;

import com.jbrod.ecommerce_api.dto.reportes.*;
import com.jbrod.ecommerce_api.dto.suspension.SuspensionHistorialDto;
import com.jbrod.ecommerce_api.dto.usuario.NotificacionHistorialDto;
import com.jbrod.ecommerce_api.servicios.ReportesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador para la gestión de Reportes de Administrador.
 * Ruta base: /api/reportes
 */
@RestController
@RequestMapping("/api/reportes")
public class ReportesController {

    @Autowired
    private ReportesService reportesService;

    /**
     * Endpoint para obtener el Top 10 de productos más vendidos en un intervalo.
     * GET /api/reportes/top-productos-vendidos?fechaInicio=...&fechaFin=...
     * Solo accesible por el rol ADMINISTRADOR.
     */
    @GetMapping("/top-productos-vendidos")
    public ResponseEntity<List<ProductoVendidoDto>> obtenerTop10Productos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {

        List<ProductoVendidoDto> reporte = reportesService.obtenerTop10ProductosVendidos(fechaInicio, fechaFin);

        if (reporte.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content si no hay datos
        }

        return ResponseEntity.ok(reporte); // 200 OK con la lista
    }


    /**
     * Endpoint para obtener el Top 5 de clientes (compradores) con más monto de compra.
     * GET /api/reportes/top-clientes-compradores?fechaInicio=...&fechaFin=...
     */
    @GetMapping("/top-clientes-compradores")
    public ResponseEntity<List<ClienteGananciaProyeccion>> obtenerTop5ClientesCompradores(

            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {

        List<ClienteGananciaProyeccion> reporte = reportesService.obtenerTop5ClientesCompradores(fechaInicio, fechaFin);

        if (reporte.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(reporte);
    }

    /**
     * Endpoint para obtener el Top 5 de vendedores (clientes comunes) con más unidades vendidas.
     * GET /api/reportes/top-vendedores?fechaInicio=...&fechaFin=...
     */
    @GetMapping("/top-vendedores")
    public ResponseEntity<List<VendedorMasVentasProyeccion>> obtenerTop5Vendedores(
                                                                                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
                                                                                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {

        List<VendedorMasVentasProyeccion> reporte = reportesService.obtenerTop5Vendedores(fechaInicio, fechaFin);

        if (reporte.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(reporte);
    }


    /**
     * Endpoint para obtener el Top 10 de clientes (compradores) con más pedidos.
     * GET /api/reportes/top-clientes-pedidos?fechaInicio=...&fechaFin=...
     */
    @GetMapping("/top-clientes-pedidos")
    public ResponseEntity<List<ClientePedidoProyeccion>> obtenerTop10ClientesMasPedidos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {

        List<ClientePedidoProyeccion> reporte = reportesService.obtenerTop10ClientesMasPedidos(fechaInicio, fechaFin);

        if (reporte.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(reporte);
    }

    /**
     * Endpoint para obtener el Top 10 de vendedores (clientes) con más productos a la venta.
     * GET /api/reportes/top-vendedores-productos
     */
    @GetMapping("/top-vendedores-productos")
    public ResponseEntity<List<VendedorProductosProyeccion>> obtenerTop10VendedoresConMasProductos() {

        List<VendedorProductosProyeccion> reporte = reportesService.obtenerTop10VendedoresConMasProductos();

        if (reporte.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(reporte);
    }

    /**
     * Endpoint para obtener el historial completo de sanciones.
     * GET /api/reportes/historial-sanciones
     */
    @GetMapping("/historial-sanciones")
    public ResponseEntity<List<SuspensionHistorialDto>> obtenerHistorialSuspensiones() {

        List<SuspensionHistorialDto> historial = reportesService.obtenerHistorialSuspensiones();

        if (historial.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(historial);
    }

    /**
     * Endpoint para obtener el historial completo de notificaciones.
     * GET /api/reportes/historial-notificaciones
     */
    @GetMapping("/historial-notificaciones")
    public ResponseEntity<List<NotificacionHistorialDto>> obtenerHistorialNotificaciones() {

        List<NotificacionHistorialDto> historial = reportesService.obtenerHistorialNotificaciones();

        if (historial.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(historial);
    }
}