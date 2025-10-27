package com.jbrod.ecommerce_api.servicios;


import com.jbrod.ecommerce_api.dto.reportes.ClienteGananciaProyeccion;
import com.jbrod.ecommerce_api.dto.reportes.ClientePedidoProyeccion;
import com.jbrod.ecommerce_api.dto.reportes.ProductoVendidoDto;
import com.jbrod.ecommerce_api.dto.reportes.VendedorMasVentasProyeccion;
import com.jbrod.ecommerce_api.repositorios.ingresos.DetalleVentaVendedorRepository;
import com.jbrod.ecommerce_api.repositorios.pedido.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportesService {

    @Autowired
    private DetalleVentaVendedorRepository detalleVentaRepository;
    @Autowired
    private PedidoRepository pedidoRepository;

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
}