package com.jbrod.ecommerce_api.repositorios.productos;

import com.jbrod.ecommerce_api.dto.reportes.VendedorProductosProyeccion;
import com.jbrod.ecommerce_api.modelos.Usuario;
import com.jbrod.ecommerce_api.modelos.productos.EstadoAprobacionProducto;
import com.jbrod.ecommerce_api.modelos.productos.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la gestión de Productos.
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Método para obtener productos por vendedor (útil para el dashboard de usuario común)
    List<Producto> findByVendedor(Usuario vendedor);

    // Método para obtener productos que están APROBADOS y tienen stock > 0 (para el catálogo principal)
    List<Producto> findByEstadoAndStockGreaterThan(EstadoAprobacionProducto estado, Integer stock);

    /**
     * Método de búsqueda para obtener productos con estado de aprobación coincide con el nombre dado.
     * Útil para buscar todos los productos 'pendiente' o 'aprobado' :33.
     */
    List<Producto> findByEstadoNombre(String nombreEstado);


    /**
     * Reporte Top 10 clientes (vendedores) con más productos listados a la venta.
     */
    @Query(value = """
        SELECT
            u.nombre AS nombreVendedor,
            COUNT(p.id_producto) AS totalProductosListados -- Contamos los productos por vendedor
        FROM
            producto p
        JOIN
            usuario u ON p.id_vendedor = u.id_usuario
        GROUP BY
            u.id_usuario, u.nombre
        ORDER BY
            totalProductosListados DESC
        LIMIT 10
    """, nativeQuery = true)
    List<VendedorProductosProyeccion> findTop10VendedoresConMasProductos();

}
