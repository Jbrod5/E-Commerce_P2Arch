package com.jbrod.ecommerce_api.repositorios.productos;

import com.jbrod.ecommerce_api.modelos.productos.SolicitudProducto;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio para la entidad SolicitudProducto.
 */
public interface SolicitudProductoRepository extends JpaRepository<SolicitudProducto, Long> {

}
