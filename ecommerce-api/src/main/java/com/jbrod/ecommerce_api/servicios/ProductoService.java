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
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal; // IMPORTANTE: Importación necesaria para las correcciones

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

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
     * Crea un nuevo producto, subiendo la imagen al sistema de archivos local y
     * asignándole el estado 'pendiente'.
     * @param dto Datos del producto.
     * @param username Correo del usuario autenticado (vendedor).
     * @param imagenFile Archivo de la imagen a subir.
     * @return Producto guardado.
     */
    @Transactional
    public Producto crearProducto(ProductoCreacionDTO dto, String username, MultipartFile imagenFile) throws IOException {
        // 1. Subir la imagen y obtener su URL pública
        // El AlmacenamientoArchivosService se encarga de guardar el archivo y devolver la URL.
        String imagenUrl = almacenamientoArchivosService.uploadFile(imagenFile);

        // 2. Obtener la entidad de Usuario (vendedor)
        Usuario vendedor = usuarioServicio.obtenerUsuarioPorCorreo(username)
                .orElseThrow(() -> new NoSuchElementException("Vendedor no encontrado: " + username));

        // 3. Obtener el estado 'pendiente'
        EstadoAprobacionProducto estadoPendiente = estadoAprobacionProductoRepository.findByNombre("pendiente")
                .orElseThrow(() -> new IllegalStateException("Estado 'pendiente' no configurado en la base de datos."));

        // 4. Obtener la Categoría
        Categoria categoria = categoriaRepository.findById(dto.getIdCategoria())
                .orElseThrow(() -> new NoSuchElementException("Categoría no encontrada con ID: " + dto.getIdCategoria()));

        // 5. Mapear DTO a Entidad Producto
        Producto nuevoProducto = new Producto();
        nuevoProducto.setNombre(dto.getNombre());
        nuevoProducto.setDescripcion(dto.getDescripcion());

        // --- CORRECCIÓN 1: Convertir Double (del DTO) a BigDecimal (de la Entidad) ---
        // Asumiendo que dto.getPrecio() devuelve Double.
        if (dto.getPrecio() != null) {
            nuevoProducto.setPrecio(BigDecimal.valueOf(dto.getPrecio()));
        } else {
            // Opcional: manejar el caso donde el precio es nulo, aunque la DB lo requiere NOT NULL.
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

        // --- CORRECCIÓN 2: Asignar BigDecimal.ZERO en lugar de 0.0 (Double) ---
        nuevoProducto.setPromedioCalificaciones(BigDecimal.ZERO);

        // Valores por defecto de la DB
        nuevoProducto.setCantidadCompras(0);

        // 6. Guardar y retornar
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

    // Aquí irían otros métodos del servicio (crear, buscar por catálogo, etc.)
}
