package com.jbrod.ecommerce_api.servicios;

import com.jbrod.ecommerce_api.dto.carrito.CarritoItemDto;
import com.jbrod.ecommerce_api.dto.carrito.CarritoResponseDto;
import com.jbrod.ecommerce_api.dto.carrito.DetalleCarritoResponseDto;
import com.jbrod.ecommerce_api.modelos.carrito.Carrito;
import com.jbrod.ecommerce_api.modelos.carrito.DetalleCarrito;
import com.jbrod.ecommerce_api.modelos.carrito.DetalleCarritoId;
import com.jbrod.ecommerce_api.modelos.productos.Producto;
import com.jbrod.ecommerce_api.repositorios.carrito.CarritoRepository;
import com.jbrod.ecommerce_api.repositorios.carrito.DetalleCarritoRepository;
import com.jbrod.ecommerce_api.repositorios.productos.ProductoRepository;
import com.jbrod.ecommerce_api.utilidades.excepciones.RecursoNoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión del carrito de compras.
 */
@Service
public class CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private DetalleCarritoRepository detalleCarritoRepository;

    @Autowired
    private ProductoRepository productoRepository;


    /**
     * Obtiene el carrito activo del usuario, o lo crea si no existe.
     * cuando se crea un nuevo carrito dentro del orElseGet.
     * * @param usuarioId ID del usuario
     * @return CarritoResponseDto
     */
    @Transactional
    public CarritoResponseDto obtenerOCrearCarrito(Long usuarioId) {
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> {
                    // Logica de creacion de un nuevo carrito integrada aquí
                    Carrito nuevoCarrito = new Carrito();
                    nuevoCarrito.setUsuarioId(usuarioId);
                    return carritoRepository.save(nuevoCarrito); // INSERT permitido por @Transactional
                });

        return mapearACarritoResponseDto(carrito);
    }


    /**
     * Agrega un producto al carrito o actualiza su cantidad.
     * @param usuarioId ID del usuario
     * @param itemDto Producto a añadir y su cantidad
     * @return CarritoResponseDto actualizado
     */
    @Transactional
    public CarritoResponseDto agregarOActualizarProducto(Long usuarioId, CarritoItemDto itemDto) {
        // asegurar que si se necesita un nuevo carrito, se crea dentro de esta transacción de escritura.
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> {
                    Carrito nuevoCarrito = new Carrito();
                    nuevoCarrito.setUsuarioId(usuarioId);
                    return carritoRepository.save(nuevoCarrito); // Crea si no existe
                });

        Producto producto = productoRepository.findById(itemDto.getProductoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", itemDto.getProductoId()));

        // Validaciones importantes: Stock y Aprobación
        if (producto.getStock() < itemDto.getCantidad()) {
            throw new IllegalArgumentException("La cantidad solicitada (" + itemDto.getCantidad() + ") excede el stock disponible (" + producto.getStock() + ").");
        }
        if (producto.getEstado().getId() != 2) { // Asumiendo ID 2 es 'aprobado' si no cambio nada en la bd vdd :c
            throw new IllegalArgumentException("El producto no está aprobado para la venta.");
        }


        DetalleCarritoId id = new DetalleCarritoId(carrito.getIdCarrito(), producto.getId());

        // Buscar el detalle del carrito existente
        Carrito finalCarrito = carrito;
        DetalleCarrito detalle = detalleCarritoRepository.findById(id)
                .orElseGet(() -> {
                    // Si no existe, crear uno nuevo, congelando el precio actual
                    DetalleCarrito nuevoDetalle = new DetalleCarrito();
                    nuevoDetalle.setId(id);
                    nuevoDetalle.setCarrito(finalCarrito);
                    nuevoDetalle.setProducto(producto);
                    nuevoDetalle.setPrecioUnitario(producto.getPrecio().setScale(2, RoundingMode.HALF_UP));
                    return nuevoDetalle;
                });

        // Establecer la cantidad (actualiza si ya existe)
        detalle.setCantidad(itemDto.getCantidad());
        detalleCarritoRepository.save(detalle);

        // Recargar el carrito y mapear para la respuesta
        Carrito finalCarrito1 = carrito;
        carrito = carritoRepository.findById(carrito.getIdCarrito()).orElseThrow(() -> new RecursoNoEncontradoException("Carrito", finalCarrito1.getIdCarrito()));
        return mapearACarritoResponseDto(carrito);
    }

    /**
     * Elimina un producto del carrito.
     * @param usuarioId ID del usuario
     * @param productoId ID del producto a eliminar
     * @return CarritoResponseDto actualizado
     */
    @Transactional
    public CarritoResponseDto eliminarProducto(Long usuarioId, Long productoId) {
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Carrito", usuarioId));

        DetalleCarritoId id = new DetalleCarritoId(carrito.getIdCarrito(), productoId);
        DetalleCarrito detalle = detalleCarritoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto en Carrito", productoId));

        detalleCarritoRepository.delete(detalle);

        // Recargar el carrito (posiblemente la lista de items se actualice automáticamente si el mapeo se hace con la entidad xd)
        carrito.getItems().removeIf(item -> item.getProducto().getId().equals(productoId));

        return mapearACarritoResponseDto(carrito);
    }

    /**
     * Borra todos los ítems del carrito.
     * @param usuarioId ID del usuario
     * @return CarritoResponseDto vacío
     */
    @Transactional
    public CarritoResponseDto borrarCarrito(Long usuarioId) {
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Carrito", usuarioId));

        detalleCarritoRepository.deleteByCarrito(carrito);
        carrito.setItems(List.of()); // Limpiar la lista de items en la entidad en memoria

        // No borramos la entidad Carrito, solo sus detalles
        return mapearACarritoResponseDto(carrito);
    }



    // --- Mapeo de Entidad a DTO y Cálculo de Total -------------------------------------------------------------------

    private CarritoResponseDto mapearACarritoResponseDto(Carrito carrito) {
        CarritoResponseDto dto = new CarritoResponseDto();
        dto.setIdCarrito(carrito.getIdCarrito());
        dto.setUsuarioId(carrito.getUsuarioId());
        // Ajustar ZonedDateTime a la zona horaria UTC si es necesario, o usar una zona específica
        dto.setFechaCreacion(carrito.getFechaCreacion().withZoneSameInstant(ZoneOffset.UTC));

        // Mapear los detalles y calcular el monto total
        if (carrito.getItems() != null && !carrito.getItems().isEmpty()) {
            List<DetalleCarritoResponseDto> itemDtos = carrito.getItems().stream()
                    .map(this::mapearADetalleCarritoResponseDto)
                    .collect(Collectors.toList());
            dto.setItems(itemDtos);

            // Calcular el monto total
            BigDecimal montoTotal = itemDtos.stream()
                    .map(DetalleCarritoResponseDto::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            dto.setMontoTotal(montoTotal.setScale(2, RoundingMode.HALF_UP));
        } else {
            dto.setItems(List.of());
            dto.setMontoTotal(BigDecimal.ZERO);
        }

        return dto;
    }

    private DetalleCarritoResponseDto mapearADetalleCarritoResponseDto(DetalleCarrito detalle) {
        DetalleCarritoResponseDto dto = new DetalleCarritoResponseDto();
        dto.setProductoId(detalle.getProducto().getId());
        dto.setNombreProducto(detalle.getProducto().getNombre());
        dto.setCantidad(detalle.getCantidad());
        // EXTRAER Y ESTABLECER LA URL DE LA IMAGEN DESDE EL PRODUCTO :'ccc
        dto.setImagenUrl(detalle.getProducto().getImagenUrl());

        // Usar el precio congelado en el detalle
        dto.setPrecioUnitario(detalle.getPrecioUnitario().setScale(2, RoundingMode.HALF_UP));

        // Calcular subtotal: Cantidad * PrecioUnitario
        BigDecimal cantidadDecimal = new BigDecimal(detalle.getCantidad());
        BigDecimal subtotal = detalle.getPrecioUnitario().multiply(cantidadDecimal);

        dto.setSubtotal(subtotal.setScale(2, RoundingMode.HALF_UP));
        return dto;
    }
}