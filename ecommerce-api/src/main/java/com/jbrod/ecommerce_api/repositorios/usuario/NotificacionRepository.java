package com.jbrod.ecommerce_api.repositorios.usuario;


import com.jbrod.ecommerce_api.modelos.usuario.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    /**
     * Busca todas las notificaciones para un usuario específico, ordenadas por fecha descendente.
     * @param idUsuario El ID del usuario.
     * @return Lista de Notificaciones.
     */
    List<Notificacion> findByUsuarioIdOrderByFechaDesc(Integer idUsuario);

    /**
     * Busca todas las notificaciones NO leídas para un usuario específico.
     * @param idUsuario El ID del usuario.
     * @param leida False (no leída).
     * @return Lista de Notificaciones no leídas.
     */
    List<Notificacion> findByUsuarioIdAndLeidaOrderByFechaDesc(Integer idUsuario, Boolean leida);

    /**
     * Reporte de Historial de Notificaciones: Carga todas las notificaciones
     * y usa FETCH JOIN para cargar EAGERLY el usuario destinatario.
     */
    @Query("SELECT n FROM Notificacion n JOIN FETCH n.usuario ORDER BY n.fecha DESC")
    List<Notificacion> findAllWithUsuarioEagerly();
}