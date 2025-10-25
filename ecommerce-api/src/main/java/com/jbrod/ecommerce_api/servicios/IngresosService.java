package com.jbrod.ecommerce_api.servicios;

import com.jbrod.ecommerce_api.dto.ingresos.DetalleVentaVendedorDTO;
import com.jbrod.ecommerce_api.modelos.Usuario;
import com.jbrod.ecommerce_api.modelos.ingresos.DetalleVentaVendedor;
import com.jbrod.ecommerce_api.repositorios.ingresos.DetalleVentaVendedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IngresosService {

    private final DetalleVentaVendedorRepository detalleVentaVendedorRepository;
    private final UsuarioService usuarioService; // NUEVA INYECCIÓN

    @Autowired
    public IngresosService(DetalleVentaVendedorRepository detalleVentaVendedorRepository, UsuarioService usuarioService) {
        this.detalleVentaVendedorRepository = detalleVentaVendedorRepository;
        this.usuarioService = usuarioService; // Asignación de la inyección
    }

    /**
     * Obtiene el historial de ventas detallado para un vendedor específico,
     * identificándolo por su correo electrónico.
     * @param correoVendedor El correo del usuario vendedor (Usuario.correo).
     * @return Una lista de DetalleVentaVendedorDTO.
     * @throws UsernameNotFoundException Si no se encuentra el usuario.
     */
    @Transactional(readOnly = true)
    public List<DetalleVentaVendedorDTO> obtenerHistorialVentasVendedor(String correoVendedor) {

        // 1. Obtener la entidad Usuario a partir del correo
        // Este método lanzará UsernameNotFoundException si el correo no existe.
        Usuario vendedor = usuarioService.obtenerPorCorreo(correoVendedor);
        Long vendedorId = vendedor.getId().longValue();

        // 2. Obtener la lista de entidades usando el ID interno
        List<DetalleVentaVendedor> ventas = detalleVentaVendedorRepository.findByVendedorIdOrderByFechaDesc(vendedorId);

        // 3. Mapear (transformar) cada entidad a su DTO correspondiente
        return ventas.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Método auxiliar para transformar la entidad DetalleVentaVendedor a su DTO.
     */
    private DetalleVentaVendedorDTO mapToDTO(DetalleVentaVendedor entidad) {
        DetalleVentaVendedorDTO dto = new DetalleVentaVendedorDTO();

        dto.setIdDetalleVenta(entidad.getId());

        // Se obtienen los IDs del Pedido y Producto
        dto.setIdPedido(entidad.getPedido() != null ? entidad.getPedido().getId() : null);
        dto.setIdProducto(entidad.getProducto() != null ? entidad.getProducto().getId() : null);

        // Acceder al nombre del producto
        dto.setNombreProducto(entidad.getProducto() != null ? entidad.getProducto().getNombre() : "Producto Desconocido");

        // Datos financieros y de cantidad
        dto.setGananciaVendedor(entidad.getGananciaVendedor());
        dto.setCantidad(entidad.getCantidad());
        dto.setPrecioUnitario(entidad.getPrecioUnitario());
        dto.setSubtotal(entidad.getSubtotal());
        dto.setFecha(entidad.getFecha());

        return dto;
    }
}
