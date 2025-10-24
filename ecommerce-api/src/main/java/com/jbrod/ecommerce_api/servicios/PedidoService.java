package com.jbrod.ecommerce_api.servicios;

import com.jbrod.ecommerce_api.dto.pedido.CheckoutRequestDto;
import com.jbrod.ecommerce_api.dto.pedido.ItemPedidoDto;
import com.jbrod.ecommerce_api.dto.pedido.PedidoDetalleDto;
import com.jbrod.ecommerce_api.dto.pedido.PedidoResumenDto;
import com.jbrod.ecommerce_api.dto.pedido.PedidoResponseDto;
import com.jbrod.ecommerce_api.modelos.carrito.Carrito;
import com.jbrod.ecommerce_api.modelos.carrito.DetalleCarrito;
import com.jbrod.ecommerce_api.modelos.pedidos.*;
import com.jbrod.ecommerce_api.modelos.productos.Producto;
import com.jbrod.ecommerce_api.modelos.usuario.Tarjetas;
import com.jbrod.ecommerce_api.repositorios.carrito.CarritoRepository;
import com.jbrod.ecommerce_api.repositorios.carrito.DetalleCarritoRepository;
import com.jbrod.ecommerce_api.repositorios.pedido.DetalleVentaVendedorRepository;
import com.jbrod.ecommerce_api.repositorios.pedido.EstadoPedidoRepository;
import com.jbrod.ecommerce_api.repositorios.pedido.ListaProductoPedidoRepository;
import com.jbrod.ecommerce_api.repositorios.pedido.PedidoRepository;
import com.jbrod.ecommerce_api.repositorios.pedido.RecaudacionPlataformaRepository;
import com.jbrod.ecommerce_api.repositorios.productos.ProductoRepository;
import com.jbrod.ecommerce_api.repositorios.usuario.TarjetasRepository;
import com.jbrod.ecommerce_api.utilidades.excepciones.RecursoNoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    // 5% de comisión para la plataforma
    private static final BigDecimal COMISION_PLATAFORMA = new BigDecimal("0.05");

    // ... (Inyección de dependencias existentes)
    @Autowired private CarritoRepository carritoRepository;
    @Autowired private DetalleCarritoRepository detalleCarritoRepository;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private PedidoRepository pedidoRepository;
    @Autowired private ListaProductoPedidoRepository listaProductoPedidoRepository;
    @Autowired private DetalleVentaVendedorRepository detalleVentaVendedorRepository;
    @Autowired private TarjetasRepository tarjetasRepository;
    @Autowired private EstadoPedidoRepository estadoPedidoRepository;
    @Autowired private RecaudacionPlataformaRepository recaudacionPlataformaRepository;

    /**
     * Procesa la compra de un usuario: valida stock, crea el pedido, actualiza inventario
     * y registra ventas/comisiones.
     * * @param usuarioId ID del usuario que compra (obtenido del token JWT)
     * @param checkoutDto Datos de la compra (tarjeta, dirección)
     * @return PedidoResponseDto de la compra exitosa.
     */
    @Transactional(rollbackFor = Exception.class) // Aseguramos que cualquier excepción revierta todo.
    public PedidoResponseDto procesarCheckout(Long usuarioId, CheckoutRequestDto checkoutDto) {

        // --- 1. OBTENER Y VALIDAR RECURSOS ---

        // Obtener Carrito y verificar ítems
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Carrito", usuarioId));

        List<DetalleCarrito> items = carrito.getItems();
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("El carrito está vacío. No se puede procesar el pedido.");
        }

        // Obtener Tarjeta y Estado Inicial del Pedido
        Tarjetas tarjetaUsada = tarjetasRepository.findById(checkoutDto.getTarjetaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Tarjeta", checkoutDto.getTarjetaId()));

        // Asume ID 1 es 'en curso' (según tu script SQL)
        EstadoPedido estadoInicial = estadoPedidoRepository.findById(1)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estado Pedido Inicial", 1L));

        // Calcular el monto total y validar stock final antes de proceder
        BigDecimal montoTotal = BigDecimal.ZERO;

        // PRE-VERIFICACIÓN DE STOCK (CRÍTICOOOOOOOOOOOOOOOOO)
        for (DetalleCarrito item : items) {
            Producto producto = item.getProducto();

            if (producto.getStock() < item.getCantidad()) {
                throw new IllegalArgumentException("Stock insuficiente para el producto: " + producto.getNombre() +
                        ". Solicitaste " + item.getCantidad() + ", solo quedan " + producto.getStock() + " unidades.");
            }

            BigDecimal subtotal = item.getPrecioUnitario().multiply(new BigDecimal(item.getCantidad()));
            montoTotal = montoTotal.add(subtotal);
        }

        // --- 2. CREAR Y GUARDAR LA ENTIDAD PEDIDO ---

        Pedido pedido = new Pedido();
        pedido.setUsuarioId(usuarioId);
        pedido.setMontoTotal(montoTotal.setScale(2, RoundingMode.HALF_UP));
        pedido.setTarjetaUsada(tarjetaUsada);
        pedido.setEstado(estadoInicial);
        pedido.setDireccion(checkoutDto.getDireccion());
        // Estimamos la entrega para 3 días después
        pedido.setFechaEntregaEstimada(LocalDateTime.now().plusDays(3));
        pedido.setFechaRealizacion(LocalDateTime.now());

        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // --- 3. PROCESAR ÍTEMS, INVENTARIO Y COMISIONES ---

        for (DetalleCarrito item : items) {
            Producto producto = item.getProducto();
            BigDecimal cantidadDecimal = new BigDecimal(item.getCantidad());
            BigDecimal precioUnitario = item.getPrecioUnitario().setScale(2, RoundingMode.HALF_UP);
            BigDecimal subtotal = precioUnitario.multiply(cantidadDecimal).setScale(2, RoundingMode.HALF_UP);

            // 3a. Guardar en lista_producto_pedido (Copia del carrito al pedido)
            ListaProductoPedido listaItem = new ListaProductoPedido();
            listaItem.setPedido(pedidoGuardado);
            listaItem.setProducto(producto);
            listaItem.setCantidad(item.getCantidad());
            listaItem.setPrecioUnitario(precioUnitario);
            listaItem.setSubtotal(subtotal);
            listaProductoPedidoRepository.save(listaItem);

            // 3b. Actualizar Stock e historial de compras del Producto (Inventario)
            producto.setStock(producto.getStock() - item.getCantidad());
            producto.setCantidadCompras(producto.getCantidadCompras() + item.getCantidad());
            productoRepository.save(producto); // Guarda el cambio de stock

            // 3c. Calcular y Guardar Detalle de Venta (Vendedor y Plataforma)
            BigDecimal comisionMonto = subtotal.multiply(COMISION_PLATAFORMA).setScale(2, RoundingMode.HALF_UP);
            BigDecimal gananciaVendedor = subtotal.subtract(comisionMonto).setScale(2, RoundingMode.HALF_UP);

            DetalleVentaVendedor detalleVenta = new DetalleVentaVendedor();
            detalleVenta.setPedido(pedidoGuardado);
            detalleVenta.setProducto(producto);
            detalleVenta.setVendedorId(producto.getVendedor().getId().longValue());
            detalleVenta.setComisionPlataforma(comisionMonto);
            detalleVenta.setGananciaVendedor(gananciaVendedor);
            detalleVenta.setCantidad(item.getCantidad());
            detalleVenta.setPrecioUnitario(precioUnitario);
            detalleVenta.setSubtotal(subtotal);
            detalleVenta.setFecha(LocalDateTime.now());

            detalleVentaVendedorRepository.save(detalleVenta);

            // 3d. Guardar Recaudación de la Plataforma (Registro de Ingreso)
            RecaudacionPlataforma recaudacion = new RecaudacionPlataforma();
            recaudacion.setMonto(comisionMonto);
            recaudacion.setDescripcion("Comisión (5%) por venta del producto '" + producto.getNombre() + "' en Pedido #" + pedidoGuardado.getId());
            recaudacion.setFecha(LocalDateTime.now());
            recaudacionPlataformaRepository.save(recaudacion);
        }

        // --- 4. LIMPIEZA ---

        // Eliminar todos los detalles del carrito. El carrito queda vacío pero la entidad existe.
        detalleCarritoRepository.deleteByCarrito(carrito);

        // --- 5. DEVOLVER RESPUESTA ---

        PedidoResponseDto responseDto = new PedidoResponseDto();
        responseDto.setIdPedido(pedidoGuardado.getId());
        responseDto.setMontoTotal(pedidoGuardado.getMontoTotal());
        responseDto.setDireccion(pedidoGuardado.getDireccion());
        responseDto.setFechaEntregaEstimada(pedidoGuardado.getFechaEntregaEstimada());

        return responseDto;
    }









    /**
     * Obtiene la lista de pedidos de un usuario (Resumen).
     * @param usuarioId ID del usuario.
     * @return Lista de PedidoResumenDto.
     */
    @Transactional
    public List<PedidoResumenDto> obtenerPedidosPorUsuario(Long usuarioId) {
        // Usamos el método de seguridad que creamos en PedidoRepository
        List<Pedido> pedidos = pedidoRepository.findByUsuarioIdOrderByFechaRealizacionDesc(usuarioId);

        // Mapeamos las entidades a los DTOs de resumen
        return pedidos.stream()
                .map(this::mapearAResumen)
                .collect(Collectors.toList());
    }

    private PedidoResumenDto mapearAResumen(Pedido pedido) {
        PedidoResumenDto dto = new PedidoResumenDto();
        dto.setIdPedido(pedido.getId());
        dto.setMontoTotal(pedido.getMontoTotal());
        // IMPORTANTE: Asume que Pedido tiene relación (getNombreEstado()) con EstadoPedido
        dto.setEstadoNombre(pedido.getEstado().getNombre());
        dto.setFechaRealizacion(pedido.getFechaRealizacion());
        dto.setDireccion(pedido.getDireccion());
        return dto;
    }


    // -----------------------------------------------------------------------------------
    // NUEVO: OBTENER DETALLE DE PEDIDO
    // -----------------------------------------------------------------------------------
    /**
     * Obtiene el detalle de un pedido específico, validando que pertenezca al usuario.
     * @param pedidoId ID del pedido.
     * @param usuarioId ID del usuario.
     * @return PedidoDetalleDto con todos los datos.
     */
    @Transactional
    public PedidoDetalleDto obtenerDetallePedido(Long pedidoId, Long usuarioId) {

        // 1. BUSCAR PEDIDO Y VALIDAR PROPIEDAD (SEGURIDAD CRÍTICA)
        Pedido pedido = pedidoRepository.findByIdAndUsuarioId(pedidoId, usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido", pedidoId));

        // 2. MAPEAR A DTO DE DETALLE
        return mapearADetalle(pedido);
    }

    private PedidoDetalleDto mapearADetalle(Pedido pedido) {
        PedidoDetalleDto detalleDto = new PedidoDetalleDto();

        // Información básica y fechas
        detalleDto.setIdPedido(pedido.getId());
        detalleDto.setMontoTotal(pedido.getMontoTotal());
        detalleDto.setEstadoNombre(pedido.getEstado().getNombre());
        detalleDto.setFechaRealizacion(pedido.getFechaRealizacion());
        detalleDto.setFechaEntregaEstimada(pedido.getFechaEntregaEstimada());
        detalleDto.setFechaEntregaReal(pedido.getFechaEntregaReal());
        detalleDto.setDireccion(pedido.getDireccion());

        // 3. OBTENER PARTE VISIBLE DE LA TARJETA (JOIN IMPLÍCITO)
        // La entidad Pedido tiene la referencia a la Tarjeta (tarjeta_usada)
        Tarjetas tarjeta = tarjetasRepository.findById(pedido.getTarjetaUsada().getId())
                .orElse(null);

        if (tarjeta != null) {
            detalleDto.setTarjetaParteVisible(tarjeta.getParteVisible());
        } else {
            detalleDto.setTarjetaParteVisible("Tarjeta Eliminada");
        }

        // 4. MAPEAR ITEMS DEL PEDIDO
        // Asumo que la entidad Pedido tiene un getItems() que devuelve List<ListaProductoPedido>
        List<ListaProductoPedido> items = pedido.getItems();


        List<ItemPedidoDto> itemDtos = items.stream()
                .map(this::mapearAItemDto)
                .collect(Collectors.toList());

        detalleDto.setItems(itemDtos);

        return detalleDto;
    }

    private ItemPedidoDto mapearAItemDto(ListaProductoPedido item) {
        ItemPedidoDto dto = new ItemPedidoDto();
        dto.setIdProducto(item.getProducto().getId());
        dto.setCantidad(item.getCantidad());
        dto.setPrecioUnitario(item.getPrecioUnitario());
        dto.setSubtotal(item.getSubtotal());

        // Obtener el nombre del producto (JOIN a Producto)
        dto.setNombreProducto(item.getProducto().getNombre());

        return dto;
    }
}