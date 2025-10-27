package com.jbrod.ecommerce_api.repositorios.ingresos;

import com.jbrod.ecommerce_api.dto.reportes.ProductoVendidoDto;
import com.jbrod.ecommerce_api.dto.reportes.VendedorMasVentasProyeccion;
import com.jbrod.ecommerce_api.modelos.ingresos.DetalleVentaVendedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DetalleVentaVendedorRepository extends JpaRepository<DetalleVentaVendedor, Long> {
    /**
     * Busca todos los registros de ventas asociados a un vendedor específico
     * usando el ID de usuario del vendedor, ordenados por fecha de forma descendente.
     * * Spring Data JPA lo traduce a:
     * SELECT d FROM DetalleVentaVendedor d WHERE d.vendedorId = :vendedorId ORDER BY d.fecha DESC
     * * @param vendedorId El ID del usuario que es el vendedor.
     * @return Una lista de DetalleVentaVendedor (las ventas del vendedor).
     */
    List<DetalleVentaVendedor> findByVendedorIdOrderByFechaDesc(Long vendedorId);


    /**
     * Reporte Top 10 productos más vendidos en un intervalo de tiempo.
     * @param fechaInicio Fecha y hora de inicio del intervalo.
     * @param fechaFin Fecha y hora de fin del intervalo.
     * @return Lista de ProductoVendidoDto con el nombre del producto y el total vendido.
     */
    @Query(value = """
        SELECT new com.jbrod.ecommerce_api.dto.reportes.ProductoVendidoDto(
            p.nombre,
            SUM(dvv.cantidad)
        )
        FROM DetalleVentaVendedor dvv
        JOIN dvv.producto p
        WHERE dvv.fecha >= :fechaInicio AND dvv.fecha <= :fechaFin
        GROUP BY p.id, p.nombre
        ORDER BY SUM(dvv.cantidad) DESC
        LIMIT 10
    """)
    List<ProductoVendidoDto> findTop10ProductosMasVendidos(LocalDateTime fechaInicio, LocalDateTime fechaFin);


    /**
     * Reporte Top 5 clientes (vendedores) que más productos han vendido en un intervalo.
     */
    @Query(value = """
        SELECT
            u.nombre AS nombreVendedor,
            SUM(dvv.cantidad) AS totalUnidadesVendidas
        FROM
            detalle_venta_vendedor dvv
        JOIN
            usuario u ON dvv.vendedor = u.id_usuario
        WHERE
            dvv.fecha >= :fechaInicio AND dvv.fecha <= :fechaFin
        GROUP BY
            u.id_usuario, u.nombre
        ORDER BY
            totalUnidadesVendidas DESC
        LIMIT 5
    """, nativeQuery = true)
    List<VendedorMasVentasProyeccion> findTop5VendedoresMasVentas(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );
}