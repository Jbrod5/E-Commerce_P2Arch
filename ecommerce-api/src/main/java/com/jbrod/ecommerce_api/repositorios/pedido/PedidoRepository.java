package com.jbrod.ecommerce_api.repositorios.pedido;


import com.jbrod.ecommerce_api.dto.reportes.ClienteGananciaProyeccion;
import com.jbrod.ecommerce_api.dto.reportes.ClientePedidoProyeccion;
import com.jbrod.ecommerce_api.modelos.pedidos.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    /**
     * Encuentra todos los pedidos de un usuario, ordenados por fecha de realización.
     * Esto será usado para la vista de "Mis Pedidos" (Resumen).
     */
    List<Pedido> findByUsuarioIdOrderByFechaRealizacionDesc(Long usuarioId);

    /**
     * Encuentra un pedido específico, asegurando que pertenezca al usuario.
     */
    Optional<Pedido> findByIdAndUsuarioId(Long id, Long usuarioId);



    /**
     * Reporte Top 5 clientes (compradores) que más ganancias (monto total de pedidos) generan.
     * Calcula el monto total y la ganancia de la plataforma (5%).
     */
    @Query(value = """
        SELECT
            u.nombre AS nombreCliente,
            SUM(p.monto_total) AS montoTotalCompras,
            SUM(p.monto_total) * 0.05 AS gananciaPlataforma -- Cálculo del 5% de comisión de la plataforma
        FROM
            pedido p
        JOIN
            usuario u ON p.usuario = u.id_usuario
        WHERE
            p.fecha_realizacion >= :fechaInicio AND p.fecha_realizacion <= :fechaFin
        GROUP BY
            u.id_usuario, u.nombre
        ORDER BY
            montoTotalCompras DESC
        LIMIT 5
    """, nativeQuery = true)
    List<ClienteGananciaProyeccion> findTop5ClientesMasGanancias(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );

    /**
     * Reporte Top 10 clientes (compradores) con más pedidos realizados en un intervalo.
     */
    @Query(value = """
        SELECT
            u.nombre AS nombreCliente,
            COUNT(p.id_pedido) AS totalPedidos -- Contamos los pedidos
        FROM
            pedido p
        JOIN
            usuario u ON p.usuario = u.id_usuario
        WHERE
            p.fecha_realizacion >= :fechaInicio AND p.fecha_realizacion <= :fechaFin
        GROUP BY
            u.id_usuario, u.nombre
        ORDER BY
            totalPedidos DESC
        LIMIT 10
    """, nativeQuery = true)
    List<ClientePedidoProyeccion> findTop10ClientesMasPedidos(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );
}