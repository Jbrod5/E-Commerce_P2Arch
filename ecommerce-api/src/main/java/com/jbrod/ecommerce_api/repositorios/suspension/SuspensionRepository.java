package com.jbrod.ecommerce_api.repositorios.suspension;


import com.jbrod.ecommerce_api.modelos.moderador.Suspension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SuspensionRepository extends JpaRepository<Suspension, Long> {

    // Método útil para el reporte de historial de sanciones
    List<Suspension> findAllByOrderByFechaSuspensionDesc();


    /**
     * Busca todas las suspensiones por el ID del usuario y las ordena
     * de la más reciente a la más antigua.
     * El nombre de este método es crucial para que JPA lo entienda.
     */
    List<Suspension> findByUsuarioIdUsuarioOrderByFechaSuspensionDesc(Long idUsuario);
}