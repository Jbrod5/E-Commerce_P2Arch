package com.jbrod.ecommerce_api.controladores;

import com.jbrod.ecommerce_api.dto.pedido.CheckoutRequestDto;
import com.jbrod.ecommerce_api.dto.pedido.PedidoDetalleDto;
import com.jbrod.ecommerce_api.dto.pedido.PedidoResponseDto;
import com.jbrod.ecommerce_api.dto.pedido.PedidoResumenDto;
import com.jbrod.ecommerce_api.servicios.PedidoService;
import com.jbrod.ecommerce_api.servicios.UsuarioService;
import com.jbrod.ecommerce_api.utilidades.excepciones.RecursoNoEncontradoException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    private final UsuarioService usuarioService; // Usado para obtener el ID del usuario a partir del correo

    /**
     * Inyección de Dependencias por Constructor.
     */
    @Autowired
    public PedidoController(PedidoService pedidoService, UsuarioService usuarioService) {
        this.pedidoService = pedidoService;
        this.usuarioService = usuarioService;
    }

    /**
     * Obtiene el ID Long del usuario a partir del objeto Principal.
     * Replicamos la lógica que tienes en CarritoController.
     */
    private Long obtenerUsuarioIdDePrincipal(Principal principal) {
        // El 'name' del Principal (o Authentication.getName()) es el correo electrónico.
        String correoUsuario = principal.getName();

        // Buscar la entidad Usuario para obtener su ID Long.
        Long usuarioId = usuarioService.obtenerUsuarioPorCorreo(correoUsuario)
                .map(u -> u.getId().longValue()) // Asumiendo que u.getId() retorna un valor que se puede convertir a Long
                .orElseThrow(() -> new NoSuchElementException("Usuario autenticado no encontrado en DB con correo: " + correoUsuario));

        return usuarioId;
    }

    // -----------------------------------------------------------------------------------------------------------------
    //  ENDPOINT: Finalizar Compra (Checkout)
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Endpoint para finalizar la compra (Checkout).
     * Ejecuta la lógica transaccional de creación de pedido, descuento de stock y registro de comisiones.
     * URL: POST /api/pedidos/checkout
     * @param checkoutDto Datos de la compra (tarjeta ID, dirección).
     * @param principal Objeto de autenticación para obtener la identidad del usuario.
     * @return Respuesta del pedido creado.
     */
    @PostMapping("/checkout")
    public ResponseEntity<?> finalizarCompra(
            @Valid @RequestBody CheckoutRequestDto checkoutDto,
            Principal principal) {

        try {
            // 1. Obtener el ID del usuario autenticado
            Long usuarioId = obtenerUsuarioIdDePrincipal(principal);

            // 2. Ejecutar la lógica transaccional de checkout
            PedidoResponseDto response = pedidoService.procesarCheckout(usuarioId, checkoutDto);

            // 3. Devolver la respuesta de éxito
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (NoSuchElementException | RecursoNoEncontradoException e) {
            // Manejo de errores de autenticación o recursos no encontrados (Usuario/Tarjeta/Carrito)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            // Manejo de errores de lógica de negocio (ej. Stock insuficiente)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // Manejo de errores generales (incluyendo problemas de transacción/BD)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar el pago: " + e.getMessage());
        }
    }



    /**
     * Endpoint para obtener el resumen de todos los pedidos realizados por el usuario autenticado.
     * URL: GET /api/pedidos
     * @param principal Objeto de autenticación para obtener la identidad del usuario.
     * @return Lista de PedidoResumenDto. // <--- ¡CAMBIO AQUÍ!
     */
    @GetMapping
    public ResponseEntity<List<PedidoResumenDto>> obtenerPedidosUsuario(Principal principal) {
        try {
            // 1. Obtener el ID del usuario autenticado
            Long usuarioId = obtenerUsuarioIdDePrincipal(principal);

            // 2. Ejecutar la lógica para obtener todos los pedidos
            // La llamada al servicio ahora es compatible con su tipo de retorno (PedidoResumenDto)
            List<PedidoResumenDto> pedidos = pedidoService.obtenerPedidosPorUsuario(usuarioId);

            // 3. Devolver la respuesta
            return ResponseEntity.ok(pedidos);

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }





    /**
     * Endpoint para obtener el detalle de un pedido específico.
     * URL: GET /api/pedidos/{id}
     * @param id ID del pedido a buscar (viene de la URL).
     * @param principal Objeto de autenticación para obtener el ID del usuario.
     * @return PedidoDetalleDto.
     */
    @GetMapping("/{id}") // MAPEA A /api/pedidos/{id}
    public ResponseEntity<?> obtenerDetallePedido(
            @PathVariable("id") Long id,
            Principal principal) {
        try {
            // 1. Obtener el ID del usuario autenticado
            Long usuarioId = obtenerUsuarioIdDePrincipal(principal);

            // 2. Ejecutar la lógica para obtener el detalle (el service ya valida la propiedad)
            PedidoDetalleDto detalle = pedidoService.obtenerDetallePedido(id, usuarioId);

            // 3. Devolver la respuesta
            return ResponseEntity.ok(detalle);

        } catch (RecursoNoEncontradoException e) {
            // Maneja el caso en que el ID no existe o no pertenece al usuario autenticado (seguridad)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // Manejo de errores generales
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}