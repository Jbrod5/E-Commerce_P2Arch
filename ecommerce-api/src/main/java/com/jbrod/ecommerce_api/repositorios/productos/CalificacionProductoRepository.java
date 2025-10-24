package com.jbrod.ecommerce_api.repositorios.productos;


import com.jbrod.ecommerce_api.modelos.Usuario;
import com.jbrod.ecommerce_api.modelos.productos.CalificacionProducto;
import com.jbrod.ecommerce_api.modelos.productos.CalificacionProductoId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CalificacionProductoRepository extends JpaRepository<CalificacionProducto, CalificacionProductoId> {

    /**
     * Encuentra todas las calificaciones y reseñas para un producto específico.
     * Se usa para obtener la lista de reseñas en el detalle del producto.
     * @param productoId ID del producto.
     * @return Lista de CalificacionProducto.
     */
    List<CalificacionProducto> findByIdProducto(Long productoId);

    /**
     * Busca si un usuario ya calificó un producto.
     * Se usa para evitar duplicados y para verificar si el usuario puede reseñar (POST/PUT).
     * @param productoId ID del producto.
     * @param usuario Usuario que intenta calificar.
     * @return Optional<CalificacionProducto>
     */
    Optional<CalificacionProducto> findByIdProductoAndUsuario(Long productoId, Usuario usuario);
}