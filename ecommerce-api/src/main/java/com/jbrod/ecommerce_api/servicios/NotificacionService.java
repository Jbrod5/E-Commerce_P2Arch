package com.jbrod.ecommerce_api.servicios;


import com.jbrod.ecommerce_api.dto.usuario.NotificacionDetalleDTO;
import com.jbrod.ecommerce_api.dto.usuario.NotificacionListDTO;
import com.jbrod.ecommerce_api.modelos.Usuario;
import com.jbrod.ecommerce_api.modelos.usuario.Notificacion;
import com.jbrod.ecommerce_api.repositorios.usuario.NotificacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final UsuarioService usuarioService;

    // Solo se inyecta NotificacionRepository y UsuarioService
    public NotificacionService(NotificacionRepository notificacionRepository, UsuarioService usuarioService) {
        this.notificacionRepository = notificacionRepository;
        this.usuarioService = usuarioService;
    }

    /**
     * Genera y guarda una nueva notificación.
     * Este método se usaría internamente.
     * Se cambió el parámetro de 'idUsuario' a 'correoDestinatario'.
     * * @param correoDestinatario Correo del destinatario.
     * @param titulo Título de la notificación.
     * @param cuerpo Contenido.
     * @return Notificacion creada.
     */
    @Transactional
    public Notificacion generarNotificacion(String correoDestinatario, String titulo, String cuerpo) {
        // Usamos obtenerUsuarioPorCorreo, el cual existe y devuelve Optional
        Usuario usuario = usuarioService.obtenerUsuarioPorCorreo(correoDestinatario)
                .orElseThrow(() -> new NoSuchElementException("Usuario con correo " + correoDestinatario + " no encontrado."));

        Notificacion notificacion = new Notificacion(usuario, titulo, cuerpo);
        return notificacionRepository.save(notificacion);
    }

    /**
     * Obtiene la lista de notificaciones para el usuario autenticado.
     * Nota: Usamos usuarioService.obtenerUsuarioPorCorreo().
     * @param correoUsuario Correo del usuario autenticado (Principal).
     * @return Lista de NotificacionListDTO.
     */
    public List<NotificacionListDTO> obtenerNotificacionesPorUsuario(String correoUsuario) {
        // Usamos obtenerUsuarioPorCorreo, el cual existe y devuelve Optional
        Usuario usuario = usuarioService.obtenerUsuarioPorCorreo(correoUsuario)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado con correo: " + correoUsuario));

        List<Notificacion> notificaciones = notificacionRepository.findByUsuarioIdOrderByFechaDesc(usuario.getId());

        // Asegúrate de usar los DTOs convertidos a POJO si es necesario (asumimos que ya lo hiciste)
        return notificaciones.stream()
                .map(n -> new NotificacionListDTO(n.getId(), n.getTitulo(), n.getFecha(), n.getLeida()))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene el número de notificaciones no leídas.
     * Nota: Usamos usuarioService.obtenerUsuarioPorCorreo().
     */
    public long contarNoLeidas(String correoUsuario) {
        // Usamos obtenerUsuarioPorCorreo, el cual existe y devuelve Optional
        Usuario usuario = usuarioService.obtenerUsuarioPorCorreo(correoUsuario)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado con correo: " + correoUsuario));

        // Nota: Si la lista es muy grande, sería mejor un método `countBy...` en el repositorio
        return notificacionRepository.findByUsuarioIdAndLeidaOrderByFechaDesc(usuario.getId(), false).size();
    }


    /**
     * Obtiene el detalle de una notificación y la marca como leída.
     * @param idNotificacion ID de la notificación.
     * @param correoUsuario Correo del usuario para verificar la propiedad.
     * @return NotificacionDetalleDTO.
     */
    @Transactional
    public NotificacionDetalleDTO obtenerYMarcarComoLeida(Long idNotificacion, String correoUsuario) {
        Notificacion notificacion = notificacionRepository.findById(idNotificacion)
                .orElseThrow(() -> new NoSuchElementException("Notificación no encontrada con ID: " + idNotificacion));

        // Validación de propiedad: Asegurarse de que el usuario autenticado es el destinatario
        if (!notificacion.getUsuario().getCorreo().equals(correoUsuario)) {
            throw new SecurityException("Acceso denegado: La notificación no pertenece a este usuario.");
        }

        // Marcar como leída si no lo está
        if (!notificacion.getLeida()) {
            notificacion.setLeida(true);
            notificacionRepository.save(notificacion); // Guarda el cambio de estado
        }

        // Mapear a DTO de detalle
        return new NotificacionDetalleDTO(
                notificacion.getId(),
                notificacion.getTitulo(),
                notificacion.getCuerpo(),
                notificacion.getFecha(),
                notificacion.getLeida()
        );
    }
}
