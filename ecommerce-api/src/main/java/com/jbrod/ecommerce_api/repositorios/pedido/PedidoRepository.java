package com.jbrod.ecommerce_api.repositorios.pedido;


import com.jbrod.ecommerce_api.modelos.pedidos.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
     * CRÍTICO para seguridad. Usado para la vista "Detalle de Pedido".
     */
    Optional<Pedido> findByIdAndUsuarioId(Long id, Long usuarioId);

}