package com.jbrod.ecommerce_api.servicios;

import com.jbrod.ecommerce_api.dto.EstadoProductoDTO;
import com.jbrod.ecommerce_api.dto.ProductoCreacionDTO;
import com.jbrod.ecommerce_api.modelos.Usuario;
import com.jbrod.ecommerce_api.modelos.productos.Categoria;
import com.jbrod.ecommerce_api.modelos.productos.EstadoAprobacionProducto;
import com.jbrod.ecommerce_api.modelos.productos.Producto;
import com.jbrod.ecommerce_api.repositorios.productos.CategoriaRepository;
import com.jbrod.ecommerce_api.repositorios.productos.EstadoAprobacionProductoRepository;
import com.jbrod.ecommerce_api.repositorios.productos.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
// REMOVIDO: import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Base64; // Importación necesaria para decodificar Base64

/**
 * Servicio para la lógica de negocio de Productos.
 * Siguiendo la nomenclatura: Producto + Service.
 */
@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final EstadoAprobacionProductoRepository estadoAprobacionProductoRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioService usuarioServicio;
    // INYECTAMOS el servicio de manejo de archivos
    private final AlmacenamientoArchivosService almacenamientoArchivosService;

    public ProductoService(ProductoRepository productoRepository,
                           EstadoAprobacionProductoRepository estadoAprobacionProductoRepositorio,
                           CategoriaRepository categoriaRepository,
                           UsuarioService usuarioServicio,
                           AlmacenamientoArchivosService almacenamientoArchivosService) {
        this.productoRepository = productoRepository;
        this.estadoAprobacionProductoRepository = estadoAprobacionProductoRepositorio;
        this.categoriaRepository = categoriaRepository;
        this.usuarioServicio = usuarioServicio;
        this.almacenamientoArchivosService = almacenamientoArchivosService; // Asignación
    }


    /**
     * NOTA: EL MÉTODO ANTIGUO CON MultipartFile HA SIDO REEMPLAZADO O ELIMINADO.
     * * Crea un nuevo producto, decodificando la imagen Base64, subiéndola al sistema
     * de archivos y asignándole el estado 'pendiente'.
     *
     * @param dto Datos del producto, incluyendo la cadena Base64 de la imagen.
     * @param username Correo del usuario autenticado (vendedor).
     * @return Producto guardado.
     * @throws IOException Si ocurre un error al guardar los bytes decodificados en disco.
     * @throws IllegalArgumentException Si la cadena Base64 no es válida.
     */
    @Transactional
    public Producto crearProductoBase64(ProductoCreacionDTO dto, String username) throws IOException, IllegalArgumentException {

        // 1. Decodificar la imagen Base64 a bytes
        String base64Image = dto.getImagenBase64();

        // Opcional: Limpiar el prefijo si el frontend lo envía (ej: "data:image/png;base64,")
        if (base64Image.startsWith("data:")) {
            base64Image = base64Image.split(",")[1];
        }

        // NOTA: Base64.getDecoder().decode() lanzará IllegalArgumentException si la cadena no es válida.
        byte[] imageBytes = Base64.getDecoder().decode(base64Image);

        // 2. Subir los bytes decodificados y obtener su URL pública
        // ESTO REQUIERE QUE AGREGUES UN NUEVO MÉTODO en AlmacenamientoArchivosService
        // para manejar byte[] en lugar de MultipartFile.
        String imagenUrl = almacenamientoArchivosService.uploadBytes(imageBytes);

        // 3. Obtener la entidad de Usuario (vendedor)
        Usuario vendedor = usuarioServicio.obtenerUsuarioPorCorreo(username)
                .orElseThrow(() -> new NoSuchElementException("Vendedor no encontrado: " + username));

        // 4. Obtener el estado 'pendiente'
        EstadoAprobacionProducto estadoPendiente = estadoAprobacionProductoRepository.findByNombre("pendiente")
                .orElseThrow(() -> new IllegalStateException("Estado 'pendiente' no configurado en la base de datos."));

        // 5. Obtener la Categoría
        Categoria categoria = categoriaRepository.findById(dto.getIdCategoria())
                .orElseThrow(() -> new NoSuchElementException("Categoría no encontrada con ID: " + dto.getIdCategoria()));

        // 6. Mapear DTO a Entidad Producto
        Producto nuevoProducto = new Producto();
        nuevoProducto.setNombre(dto.getNombre());
        nuevoProducto.setDescripcion(dto.getDescripcion());

        if (dto.getPrecio() != null) {
            nuevoProducto.setPrecio(BigDecimal.valueOf(dto.getPrecio()));
        } else {
            throw new IllegalArgumentException("El precio del producto no puede ser nulo.");
        }

        nuevoProducto.setStock(dto.getStock());
        nuevoProducto.setEsNuevo(dto.getEsNuevo());

        // ASIGNACIÓN CRUCIAL: Se usa la URL obtenida del servicio de almacenamiento
        nuevoProducto.setImagenUrl(imagenUrl);

        // Asignación de relaciones manejadas por el backend
        nuevoProducto.setVendedor(vendedor);
        nuevoProducto.setCategoria(categoria);
        nuevoProducto.setEstado(estadoPendiente);

        nuevoProducto.setPromedioCalificaciones(BigDecimal.ZERO);

        // Valores por defecto de la DB
        nuevoProducto.setCantidadCompras(0);

        // 7. Guardar y retornar
        return productoRepository.save(nuevoProducto);
    }



    /**
     * Obtiene todos los productos que han sido APROBADOS y tienen stock > 0.
     * Esta es la vista pública del Marketplace para los compradores.
     * @return Lista de productos listos para la venta.
     */
    @Transactional(readOnly = true)
    public List<Producto> obtenerProductosMarketplace() {
        // 1. Buscar el estado 'aprobado'
        EstadoAprobacionProducto estadoAprobado = estadoAprobacionProductoRepository.findByNombre("aprobado")
                .orElseThrow(() -> new IllegalStateException("Estado 'aprobado' no configurado en la base de datos."));

        // 2. Usar el repositorio para filtrar por estado y stock
        return productoRepository.findByEstadoAndStockGreaterThan(estadoAprobado, 0);
    }



    /**
     * Obtiene todos los productos subidos por un vendedor específico.
     * Esta vista es para "Mi Inventario" del vendedor, por lo que no filtra por estado.
     * @param username Correo del vendedor autenticado.
     * @return Lista de productos del vendedor.
     */
    @Transactional(readOnly = true)
    public List<Producto> obtenerProductosPorVendedor(String username) {
        // 1. Obtener la entidad Usuario a partir del correo.
        Usuario vendedor = usuarioServicio.obtenerUsuarioPorCorreo(username)
                .orElseThrow(() -> new NoSuchElementException("Vendedor no encontrado con correo: " + username));

        // 2. Usar el método del repositorio que usa la entidad Usuario
        return productoRepository.findByVendedor(vendedor);
    }






    /**
     * Lógica para que un Moderador cambie el estado de aprobación de un producto.
     * @param idProducto ID del producto a modificar.
     * @param dto DTO con el nuevo nombre del estado.
     * @return Producto modificado.
     * @throws NoSuchElementException Si el producto no existe.
     * @throws IllegalArgumentException Si el estado de aprobación es inválido.
     */
    @Transactional
    public Producto actualizarEstadoProducto(Long idProducto, EstadoProductoDTO dto) {
        // 1. Buscar el producto por ID
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new NoSuchElementException("Producto no encontrado con ID: " + idProducto));

        // 2. Buscar el estado de aprobación por nombre (asegurándose que esté en minúsculas)
        String nombreEstado = dto.getNombreEstado().toLowerCase();
        EstadoAprobacionProducto nuevoEstado = estadoAprobacionProductoRepository.findByNombre(nombreEstado)
                .orElseThrow(() -> new IllegalArgumentException("Estado de aprobación desconocido: " + nombreEstado));

        // 3. Actualizar el estado del producto
        producto.setEstado(nuevoEstado);

        return productoRepository.save(producto);
    }
}
