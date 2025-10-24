package com.jbrod.ecommerce_api.repositorios.pedido;

import com.jbrod.ecommerce_api.modelos.pedidos.RecaudacionPlataforma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecaudacionPlataformaRepository extends JpaRepository<RecaudacionPlataforma, Long> {
}