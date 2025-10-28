package com.jbrod.ecommerce_api.controladores;

import com.jbrod.ecommerce_api.modelos.pedidos.Pedido;
import com.jbrod.ecommerce_api.servicios.LogisticaService;
import com.jbrod.ecommerce_api.dto.pedido.PedidoResumenDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/logistica")
public class LogisticaController {

    @Autowired
    private LogisticaService logisticaService;

    /**
     * GET /api/logistica/pedidos
     * Obtiene todos los pedidos que necesitan gestión (en curso o enviados).
     */
    @GetMapping("/pedidos")
    public ResponseEntity<List<PedidoResumenDto>> obtenerPedidosPendientes() {
        List<Pedido> pedidos = logisticaService.obtenerPedidosPendientes();

        // Mapeo de Pedido a PedidoResumenDto
        List<PedidoResumenDto> dtos = pedidos.stream()
                .map(p -> {
                    PedidoResumenDto dto = new PedidoResumenDto();
                    dto.setIdPedido(p.getId());
                    dto.setMontoTotal(p.getMontoTotal());
                    dto.setEstadoNombre(p.getEstado().getNombre());
                    dto.setFechaRealizacion(p.getFechaRealizacion());
                    dto.setDireccion(p.getDireccion());
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    /**
     * PATCH /api/logistica/pedidos/{id}/avanzar
     * Avanza el estado del pedido al siguiente (en curso -> enviado, o enviado -> entregado).
     */
    @PatchMapping("/pedidos/{id}/avanzar")
    public ResponseEntity<?> avanzarEstadoPedido(@PathVariable Long id) {
        try {
            Pedido pedidoActualizado = logisticaService.avanzarEstadoPedido(id);
            String nuevoEstado = pedidoActualizado.getEstado().getNombre();

            return ResponseEntity.ok(Map.of("mensaje",
                    "Pedido #" + id + " actualizado. Nuevo estado: " + nuevoEstado.toUpperCase()));

        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            // Error si ya está entregado
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


}