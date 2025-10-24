package com.jbrod.ecommerce_api.repositorios.pedido;


import com.jbrod.ecommerce_api.modelos.pedidos.ListaProductoPedido;
import com.jbrod.ecommerce_api.modelos.pedidos.ListaProductoPedidoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ListaProductoPedidoRepository extends JpaRepository<ListaProductoPedido, ListaProductoPedidoId> {
}