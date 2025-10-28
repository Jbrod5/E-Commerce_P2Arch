package com.jbrod.ecommerce_api.repositorios.carrito;

import com.jbrod.ecommerce_api.modelos.carrito.Carrito;
import com.jbrod.ecommerce_api.modelos.carrito.DetalleCarrito;
import com.jbrod.ecommerce_api.modelos.carrito.DetalleCarritoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad DetalleCarrito, usando la clave compuesta DetalleCarritoId.
 */
@Repository
public interface DetalleCarritoRepository extends JpaRepository<DetalleCarrito, DetalleCarritoId> {

    /**
     * Encuentra todos los ítems de un carrito específico.
     */
    List<DetalleCarrito> findByCarrito(Carrito carrito);

    /**
     * Elimina todos los ítems de un carrito.
     */
    void deleteByCarrito(Carrito carrito);


    @Modifying
    @Query("DELETE FROM DetalleCarrito d WHERE d.carrito.id = :carritoId")
    void deleteByCarritoId(@Param("carritoId") Long carritoId);

}