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
    private final EmailService emailService;


    public NotificacionService(NotificacionRepository notificacionRepository, UsuarioService usuarioService, EmailService emailService) {
        this.notificacionRepository = notificacionRepository;
        this.usuarioService = usuarioService;
        this.emailService = emailService;
    }

    /**
     * Genera y guarda una nueva notificación.
     * @param correoDestinatario Correo del destinatario.
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
        try{
            //Enviar por correo (o al menos intentarlo xd)
            emailService.enviarCorreo(correoDestinatario, titulo, cuerpo);
        }catch (Exception e){
            e.printStackTrace();
        }


        return notificacionRepository.save(notificacion);
    }

    /**
     * Obtiene la lista de notificaciones para el usuario autenticado.
     * @param correoUsuario Correo del usuario autenticado (Principal).
     * @return Lista de NotificacionListDTO.
     */
    public List<NotificacionListDTO> obtenerNotificacionesPorUsuario(String correoUsuario) {
        // Buscar usuario con obtenerUsuarioPorCorreo, obviamente usando el correo del usuario zd
        Usuario usuario = usuarioService.obtenerUsuarioPorCorreo(correoUsuario)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado con correo: " + correoUsuario));

        List<Notificacion> notificaciones = notificacionRepository.findByUsuarioIdOrderByFechaDesc(usuario.getId());


        return notificaciones.stream()
                .map(n -> new NotificacionListDTO(n.getId(), n.getTitulo(), n.getFecha(), n.getLeida()))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene el número de notificaciones no leídas.
     * Nota: Usamos usuarioService.obtenerUsuarioPorCorreo().
     */
    public long contarNoLeidas(String correoUsuario) {
        // Buscar usuario por correo
        Usuario usuario = usuarioService.obtenerUsuarioPorCorreo(correoUsuario)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado con correo: " + correoUsuario));

        //Retornar
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

        // Asegurarse de que el usuario autenticado es el destinatario xd
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
