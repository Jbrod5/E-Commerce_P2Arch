package com.jbrod.ecommerce_api.repositorios.usuario;


import com.jbrod.ecommerce_api.modelos.usuario.Tarjetas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TarjetasRepository extends JpaRepository<Tarjetas, Long> {

    // Método útil para listar las tarjetas de un usuario específico
    List<Tarjetas> findByUsuarioId(Long usuarioId);
}