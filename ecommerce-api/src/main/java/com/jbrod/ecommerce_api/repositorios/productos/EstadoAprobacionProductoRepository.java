package com.jbrod.ecommerce_api.repositorios.productos;

import com.jbrod.ecommerce_api.modelos.productos.EstadoAprobacionProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la gestión de los estados de aprobación del producto.
 */
@Repository
public interface EstadoAprobacionProductoRepository extends JpaRepository<EstadoAprobacionProducto, Integer> {

    // Permite buscar un estado por su nombre , como  "pendiente", "aprobado".
    Optional<EstadoAprobacionProducto> findByNombre(String nombre);
}
