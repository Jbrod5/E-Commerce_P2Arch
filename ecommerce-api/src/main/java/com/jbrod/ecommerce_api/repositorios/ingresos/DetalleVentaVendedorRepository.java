package com.jbrod.ecommerce_api.repositorios.ingresos;

import com.jbrod.ecommerce_api.modelos.ingresos.DetalleVentaVendedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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

}