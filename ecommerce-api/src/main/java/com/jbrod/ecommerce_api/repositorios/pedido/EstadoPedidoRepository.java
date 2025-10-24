package com.jbrod.ecommerce_api.repositorios.pedido;


import com.jbrod.ecommerce_api.modelos.pedidos.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoPedidoRepository extends JpaRepository<EstadoPedido, Long> {
}