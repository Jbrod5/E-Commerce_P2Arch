package com.jbrod.ecommerce_api.repositorios.productos;

import com.jbrod.ecommerce_api.modelos.Usuario;
import com.jbrod.ecommerce_api.modelos.productos.EstadoAprobacionProducto;
import com.jbrod.ecommerce_api.modelos.productos.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
