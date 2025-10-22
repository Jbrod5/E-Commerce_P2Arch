package com.jbrod.ecommerce_api.repositorios.carrito;


import com.jbrod.ecommerce_api.modelos.carrito.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Carrito.
 */
@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    /**
     * Busca el Carrito activo por el ID del usuario.
     */
    Optional<Carrito> findByUsuarioId(Long usuarioId);
}
