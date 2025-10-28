package com.jbrod.ecommerce_api.controladores;

import com.jbrod.ecommerce_api.dto.carrito.CarritoItemDto;
import com.jbrod.ecommerce_api.dto.carrito.CarritoResponseDto;
import com.jbrod.ecommerce_api.servicios.CarritoService;
import com.jbrod.ecommerce_api.servicios.UsuarioService;
import com.jbrod.ecommerce_api.utilidades.excepciones.RecursoNoEncontradoException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.NoSuchElementException;

/**
 * Controlador REST para la gestión del carrito de compras.
 * Se asume que el usuario está autenticado y su ID se obtiene de 'Principal'.
 */
@RestController
@RequestMapping("/api/carrito")
public class CarritoController {


    private final CarritoService carritoService;
    private final UsuarioService usuarioService; // Dependencia necesaria para obtener el ID

    /**
     * Inyección de Dependencias por Constructor.
     * @param carritoService Servicio de lógica de carrito.
     * @param usuarioService Servicio para consultar la entidad Usuario (para obtener el ID).
     */
    public CarritoController(CarritoService carritoService, UsuarioService usuarioService) {
        this.carritoService = carritoService;
        this.usuarioService = usuarioService;
    }


    /**
     * Obtiene el ID Long del usuario a partir del objeto Principal (el token JWT validado).
     * @param principal Objeto que contiene la identidad del usuario (correo).
     * @return Long ID del usuario en la base de datos.
     * @throws NoSuchElementException Si el usuario autenticado por el token no existe en la DB.
     */
    private Long obtenerUsuarioIdDePrincipal(Principal principal) {
        // 1. El name del Principal es el correo electrónico del usuario autenticado :3
        String correoUsuario = principal.getName();

        // 2. Buscar la entidad Usuario y obtener su ID
        Long usuarioId = usuarioService.obtenerUsuarioPorCorreo(correoUsuario)
                .map(u -> u.getId().longValue())
                .orElseThrow(() -> new NoSuchElementException("Usuario autenticado no encontrado en DB con correo: " + correoUsuario));

        return usuarioId;
    }


    //  ENDPOINT: Obtener Carrito --------------------------------------------------------------------------------------


    /**
     * Obtiene el carrito activo del usuario autenticado.
     */
    @GetMapping
    public ResponseEntity<CarritoResponseDto> obtenerCarrito(Principal principal) {
        try {
            Long usuarioId = obtenerUsuarioIdDePrincipal(principal);
            CarritoResponseDto carrito = carritoService.obtenerOCrearCarrito(usuarioId);
            return ResponseEntity.ok(carrito);
        } catch (NoSuchElementException e) {
            // Si el usuario del token no existe, se devuelve 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    //  ENDPOINT: Agregar/Actualizar Producto

    /**
     * Agrega un producto al carrito o actualiza su cantidad.
     * Mapea a la funcionalidad "Agregar Producto" y "Actualizar Carrito".
     */
    @PostMapping
    public ResponseEntity<?> agregarOActualizarProducto(
            Principal principal,
            @Valid @RequestBody CarritoItemDto itemDto) {

        try {
            Long usuarioId = obtenerUsuarioIdDePrincipal(principal);
            CarritoResponseDto carritoActualizado = carritoService.agregarOActualizarProducto(usuarioId, itemDto);
            return new ResponseEntity<>(carritoActualizado, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no válido.");
        } catch (RecursoNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            // Manejo de errores como stock insuficiente o producto no aprobado
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }



    //  ENDPOINT: Eliminar Producto ------------------------------------------------------------------------------------

    /**
     * Elimina un producto específico del carrito.
     * Mapea a la funcionalidad "Eliminar Producto".
     */
    @DeleteMapping("/{productoId}")
    public ResponseEntity<?> eliminarProducto(
            Principal principal,
            @PathVariable Long productoId) {

        try {
            Long usuarioId = obtenerUsuarioIdDePrincipal(principal);
            CarritoResponseDto carritoActualizado = carritoService.eliminarProducto(usuarioId, productoId);
            return new ResponseEntity<>(carritoActualizado, HttpStatus.OK);
        } catch (NoSuchElementException | RecursoNoEncontradoException e) {
            // Si el usuario no existe o el producto no está en el carrito >:c
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    //  ENDPOINT: Borrar Carrito (Vaciar) ------------------------------------------------------------------------------

    /**
     * Borra todos los artículos del carrito.
     * Mapea a la funcionalidad "Borrar Carrito".
     */
    @DeleteMapping
    public ResponseEntity<?> borrarCarrito(Principal principal) {
        try {
            Long usuarioId = obtenerUsuarioIdDePrincipal(principal);
            CarritoResponseDto carritoVacio = carritoService.borrarCarrito(usuarioId);
            return new ResponseEntity<>(carritoVacio, HttpStatus.OK);
        } catch (NoSuchElementException | RecursoNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}