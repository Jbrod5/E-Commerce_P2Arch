package com.jbrod.ecommerce_api.repositorios.suspension;


import com.jbrod.ecommerce_api.modelos.moderador.Suspension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SuspensionRepository extends JpaRepository<Suspension, Long> {

    // Método útil para el reporte de historial de sanciones
    List<Suspension> findAllByOrderByFechaSuspensionDesc();


    /**
     * Busca todas las suspensiones por el ID del usuario y las ordena
     * de la más reciente a la más antigua.
     */
    //List<Suspension> findByUsuarioIdUsuarioOrderByFechaSuspensionDesc(Long idUsuario);

    /**
     * La consulta utiliza la propiedad 'id' de la entidad Usuario para referirse a la llave primaria.
     */
    @Query("SELECT s FROM Suspension s WHERE s.usuarioSancionado.id = :idUsuario ORDER BY s.fechaSuspension DESC")
    List<Suspension> obtenerHistorialSuspensionesPorUsuario(@Param("idUsuario") Long idUsuario);

    /**
     * Busca la única suspensión activa (activa = true) para un usuario específico.
     * Devuelve un Optional porque puede no haber ninguna activa.
     */
    Optional<Suspension> findByUsuarioSancionadoIdAndActivaTrue(Long idUsuario);


    /**
     * Devuelve todas las suspenciones usando FETCH JOIN para cargar EAGERLY el usuario sancionado
     * y el moderador dentro de la misma consulta/sesión.
     */
    @Query("SELECT s FROM Suspension s " +
            "JOIN FETCH s.usuarioSancionado " +
            "JOIN FETCH s.moderador " +
            "ORDER BY s.fechaSuspension DESC")
    List<Suspension> findAllWithUsuariosEagerly();
}