package com.jbrod.ecommerce_api.servicios;


import com.jbrod.ecommerce_api.modelos.Usuario;
import com.jbrod.ecommerce_api.modelos.pedidos.Pedido;
import com.jbrod.ecommerce_api.modelos.pedidos.EstadoPedido;
import com.jbrod.ecommerce_api.repositorios.UsuarioRepository;
import com.jbrod.ecommerce_api.repositorios.pedido.PedidoRepository;
import com.jbrod.ecommerce_api.repositorios.pedido.EstadoPedidoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class LogisticaService {

    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private EstadoPedidoRepository estadoPedidoRepository;
    @Autowired
    private NotificacionService notificacionService;
    @Autowired
    private UsuarioRepository usuarioRepository;

    // Constantes para los IDs de estadooooo
    private static final int ESTADO_EN_CURSO = 1;
    private static final int ESTADO_ENVIADO = 2;
    private static final int ESTADO_ENTREGADO = 3;

    /**
     * Obtiene todos los pedidos pendientes de entrega (en curso o enviados).
     * Usa la consulta con FETCH JOIN para evitar la LazyInitializationException.
     * @return Lista de Pedido
     */
    public List<Pedido> obtenerPedidosPendientes() {
        // Usa el nuevo método con FETCH JOIN
        return pedidoRepository.findPedidosGestionLogisticaWithEstado(ESTADO_ENTREGADO);
    }

    /**
     * Modifica el estado de un pedido al siguiente paso y genera la notificación.
     * @param idPedido ID del pedido a actualizar.
     * @return El Pedido actualizado.
     */
    @Transactional // Asegura que la DB se actualice si todo va bien :3
    public Pedido avanzarEstadoPedido(Long idPedido) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado con ID: " + idPedido));

        int estadoActualId = pedido.getEstado().getId().intValue();
        int nuevoEstadoId;

        if (estadoActualId == ESTADO_EN_CURSO) {
            nuevoEstadoId = ESTADO_ENVIADO;
        } else if (estadoActualId == ESTADO_ENVIADO) {
            nuevoEstadoId = ESTADO_ENTREGADO;
            // Si el nuevo estado es ENTREGADO, registramos la fecha real de entrega :3
            pedido.setFechaEntregaReal(LocalDateTime.now());
        } else if (estadoActualId == ESTADO_ENTREGADO) {
            throw new IllegalStateException("El pedido con ID " + idPedido + " ya ha sido entregado.");
        } else {
            throw new IllegalStateException("Estado de pedido desconocido o inválido.");
        }

        // 1. Obtener y setear el nuevo estado
        EstadoPedido nuevoEstado = estadoPedidoRepository.findById(nuevoEstadoId)
                .orElseThrow(() -> new EntityNotFoundException("Estado de pedido no encontrado."));
        pedido.setEstado(nuevoEstado);

        // 2. Guardar la actualización del pedido
        Pedido pedidoActualizado = pedidoRepository.save(pedido);

        // 3. Generar la notificación
        generarNotificacion(pedidoActualizado);

        return pedidoActualizado;
    }

    /**
     * Genera la notificación para el usuario final (Comprador).
     */
    private void generarNotificacion(Pedido pedido) {

        Usuario usuario = usuarioRepository.findById(pedido.getUsuarioId().intValue())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado para notificar."));

        String nuevoEstado = pedido.getEstado().getNombre();
        String titulo = "Actualización de Pedido #" + pedido.getId();


        String cuerpo = "Estimado " + usuario.getNombre() + "\n"
                      + "El estado de su pedido ha cambiado a: " + nuevoEstado.toUpperCase() + ".\n"
                      + "La fecha estimada de entrega es: " + pedido.getFechaEntregaEstimada().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));


        // Si es ENTREGADO, el mensaje es diferente
        if (pedido.getEstado().getId() == ESTADO_ENTREGADO) {
            cuerpo = "¡¡TU PEDIDO HA SIDO ENTREGADO!!\n"
                    +"La fecha y hora registrada como entrega es: " + pedido.getFechaEntregaReal().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }

        notificacionService.generarNotificacion(
                usuario.getCorreo(), // Para el enviar el correo :3
                titulo,
                cuerpo
        );
    }
}