package com.jbrod.ecommerce_api.servicios;

import com.jbrod.ecommerce_api.dto.EstadoProductoDTO;
import com.jbrod.ecommerce_api.dto.ProductoCreacionDTO;
import com.jbrod.ecommerce_api.dto.producto.ProductoDetalleDto;
import com.jbrod.ecommerce_api.dto.producto.ResenaDto;
import com.jbrod.ecommerce_api.dto.producto.ResenaRequestDto;
import com.jbrod.ecommerce_api.dto.solicitudes.ComentarioRechazoDto;
import com.jbrod.ecommerce_api.dto.solicitudes.DecisionModeracionDto;
import com.jbrod.ecommerce_api.dto.solicitudes.SolicitudPendienteDto;
import com.jbrod.ecommerce_api.modelos.Usuario;
import com.jbrod.ecommerce_api.modelos.productos.*;
import com.jbrod.ecommerce_api.repositorios.productos.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
// REMOVIDO: import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.io.IOException;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Base64; // Importación necesaria para decodificar Base64
import java.util.Optional;
import java.util.stream.Collectors;

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

    private final AlmacenamientoArchivosService almacenamientoArchivosService;



    private final CalificacionProductoRepository calificacionProductoRepository;
    private final SolicitudProductoRepository solicitudProductoRepository;


    private final ConfiguracionGlobalService configuracionGlobalService;
    private final NotificacionService notificacionService;

    public ProductoService(ProductoRepository productoRepository,
                           EstadoAprobacionProductoRepository estadoAprobacionProductoRepositorio,
                           CategoriaRepository categoriaRepository,
                           UsuarioService usuarioServicio,
                           AlmacenamientoArchivosService almacenamientoArchivosService,
                           CalificacionProductoRepository calificacionProductoRepository,
                           ConfiguracionGlobalService configuracionGlobalService,
                           SolicitudProductoRepository solicitudProductoRepository,
                           NotificacionService notificacionService) {
        this.productoRepository = productoRepository;
        this.estadoAprobacionProductoRepository = estadoAprobacionProductoRepositorio;
        this.categoriaRepository = categoriaRepository;
        this.usuarioServicio = usuarioServicio;
        this.almacenamientoArchivosService = almacenamientoArchivosService;
        this.calificacionProductoRepository = calificacionProductoRepository;

        this.configuracionGlobalService = configuracionGlobalService;
        this.solicitudProductoRepository = solicitudProductoRepository;
        this.notificacionService = notificacionService;
    }


    /**
     * * Crea un nuevo producto, decodificando la imagen Base64, guardandola en la pecerda, asignándole el estado 'pendiente' y registrando la solicitud.
     *
     * @param dto Datos del producto, incluyendo la cadena Base64 de la imagen.
     * @param username Correo del usuario autenticado (vendedor).
     * @return Producto guardado.
     */
    @Transactional
    public Producto crearProductoBase64(ProductoCreacionDTO dto, String username) throws IOException, IllegalArgumentException {

        // 1. Decodificar la imagen Base64 y obtener la URL
        String base64Image = dto.getImagenBase64();

        if (base64Image.startsWith("data:")) {
            base64Image = base64Image.split(",")[1];
        }

        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        String imagenUrl = almacenamientoArchivosService.uploadBytes(imageBytes);

        // 2. Obtener entidades
        Usuario vendedor = usuarioServicio.obtenerUsuarioPorCorreo(username)
                .orElseThrow(() -> new NoSuchElementException("Vendedor no encontrado: " + username));

        EstadoAprobacionProducto estadoPendiente = estadoAprobacionProductoRepository.findByNombre("pendiente")
                .orElseThrow(() -> new IllegalStateException("Estado 'pendiente' no configurado en la base de datos."));

        Categoria categoria = categoriaRepository.findById(dto.getIdCategoria())
                .orElseThrow(() -> new NoSuchElementException("Categoría no encontrada con ID: " + dto.getIdCategoria()));

        // 3. Mapear DTO a Entidad Producto
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
        nuevoProducto.setImagenUrl(imagenUrl);
        nuevoProducto.setVendedor(vendedor);
        nuevoProducto.setCategoria(categoria);
        nuevoProducto.setEstado(estadoPendiente);
        nuevoProducto.setPromedioCalificaciones(BigDecimal.ZERO);
        nuevoProducto.setCantidadCompras(0);

        // 4. Guardar Producto
        Producto productoGuardado = productoRepository.save(nuevoProducto);

        // 5. REGISTRAR SOLICITUD DE PRODUCTO
        SolicitudProducto nuevaSolicitud = new SolicitudProducto();
        nuevaSolicitud.setProducto(productoGuardado);
        // El moderador, fecha_revision, aprobado y comentario_moderador quedan NULL
        solicitudProductoRepository.save(nuevaSolicitud);

        //GENERAR NOTIFICACION AAA
        notificacionService.generarNotificacion(
            //Correo, titulo, cuerpo
                username, "En espera de aprobacion: " + dto.getNombre(),
                "Su producto " +  dto.getNombre() + " se guardó correctemente y está en espera para ser aprobado por un moderador.\n\nEl estado de su producto es: PENDIENTE.\n\n E-CommerceGT"
        );

        return productoGuardado;
    }






    /**
     * Obtiene todos los productos que están en estado 'pendiente'.
     * @return Lista de DTOs con la información de la solicitud.
     */
    @Transactional(readOnly = true)
    public List<SolicitudPendienteDto> obtenerSolicitudesPendientes() {
        // buscar por el nombre pendiente xd
        List<Producto> productosPendientes = productoRepository.findByEstadoNombre("pendiente");

        // Mapear la lista de Producto a la lista de SolicitudPendienteDto
        return productosPendientes.stream()
                .map(producto -> {
                    // Se utiliza el servicio de configuración para transformar la URL
                    String urlConvertida = configuracionGlobalService.convertirUrlImagen(producto.getImagenUrl());
                    SolicitudPendienteDto dto = SolicitudPendienteDto.fromEntity(producto);
                    dto.setImagenUrl(urlConvertida); // Sobreescribe con la URL convertida xd
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Lógica unificada para que un Moderador apruebe o rechace un producto.
     * @param idProducto ID del producto a revisar.
     * @param dto DTO con la decisión (aprobado: true/false) y el comentario.
     * @param username Correo del moderador autenticado.
     * @return Producto actualizado.
     */
    @Transactional
    public Producto revisarProducto(Long idProducto, DecisionModeracionDto dto, String username) {
        // 1. Definir el estado y el comentario final
        final boolean esAprobacion = dto.isAprobado();
        final String nombreNuevoEstado = esAprobacion ? "aprobado" : "rechazado";

        String comentarioFinal = dto.getComentario() != null && !dto.getComentario().isBlank()
                ? dto.getComentario()
                : (esAprobacion ? "Aprobado por el moderador." : "Rechazado sin comentario específico.");

        // 2. Buscar entidades
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new NoSuchElementException("Producto no encontrado con ID: " + idProducto));

        Usuario moderador = usuarioServicio.obtenerUsuarioPorCorreo(username)
                .orElseThrow(() -> new NoSuchElementException("Moderador no encontrado."));

        EstadoAprobacionProducto nuevoEstado = estadoAprobacionProductoRepository.findByNombre(nombreNuevoEstado)
                .orElseThrow(() -> new IllegalStateException("Estado '" + nombreNuevoEstado + "' no configurado."));

        // 3. Validar estado
        if (!"pendiente".equalsIgnoreCase(producto.getEstado().getNombre())) {
            throw new IllegalStateException("El producto no está en estado 'pendiente' para ser revisado.");
        }

        // 4. Actualizar Producto
        producto.setEstado(nuevoEstado);
        Producto productoActualizado = productoRepository.save(producto);

        // 5. Actualizar SolicitudProducto (buscar la solicitud pendiente)
        SolicitudProducto solicitud = productoActualizado.getSolicitudes().stream()
                .filter(s -> s.getAprobado() == null) // La que todavia no tiene decisionnnn
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No se encontró solicitud pendiente asociada al producto."));

        solicitud.setModerador(moderador);
        solicitud.setFechaRevision(LocalDateTime.now());
        solicitud.setAprobado(esAprobacion);
        solicitud.setComentarioModerador(comentarioFinal);

        solicitudProductoRepository.save(solicitud);

        //GENERAR NOTIFICACION AAA
        notificacionService.generarNotificacion(
                //Correo, titulo, cuerpo
                producto.getVendedor().getCorreo(),
                "Deliberación sobre " + producto.getNombre() + ": "  + nombreNuevoEstado,
                "Estimado usuario, el moderador " + moderador.getNombre() + " ha evaluado su producto "+ producto.getNombre() + " y lo ha " + nombreNuevoEstado + " para su venta en nuestra plataforma.\n\n"
                + "El comentario de deliberación sobre el producto es el siguiente: \n"
                + dto.getComentario()
                + "\n\n E-CommerceGT"
        );


        return productoActualizado;
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
     * Esta vista es para Inventario del vendedor, por lo que no filtra por estado.
     * @param username Correo del vendedor autenticado.
     * @return Lista de productos del vendedor.
     */
    @Transactional(readOnly = true)
    public List<Producto> obtenerProductosPorVendedor(String username) {
        // 1. Obtener la entidad Usuario a partir del correo.
        Usuario vendedor = usuarioServicio.obtenerUsuarioPorCorreo(username)
                .orElseThrow(() -> new NoSuchElementException("Vendedor no encontrado con correo: " + username));

        // 2. Usar el metodo del repositorio que usa la entidad Usuario
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



    // RESEÑASSS
    /**
     * Obtiene el detalle completo de un producto, incluyendo la información del vendedor
     * y la lista de calificaciones/reseñas.
     * @param idProducto ID del producto.
     * @return ProductoDetalleDto con toda la información.
     */
    @Transactional(readOnly = true)
    public ProductoDetalleDto obtenerDetalleProducto(Long idProducto) {

        // 1. Buscar el producto por ID
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new NoSuchElementException("Producto no encontrado con ID: " + idProducto));

        // Filtrar solo por productos 'aprobados'
        if (!"aprobado".equalsIgnoreCase(producto.getEstado().getNombre())) {
            throw new IllegalArgumentException("El producto no está aprobado para la venta.");
        }

        // 2. Buscar todas las calificaciones y reseñas
        List<CalificacionProducto> calificaciones = calificacionProductoRepository.findByIdProducto(idProducto);

        // 3. Mapear reseñas a DTOs
        List<ResenaDto> resenasDto = calificaciones.stream()
                .map(this::mapearAResenaDto)
                .collect(Collectors.toList());

        // 4. Mapear el producto a DTO de detalle
        ProductoDetalleDto detalleDto = mapearADetalleDto(producto);

        // 5. Asignar reseñas y contar
        detalleDto.setResenas(resenasDto);
        detalleDto.setCantidadResenas(resenasDto.size());

        return detalleDto;
    }

    // Mapea CalificacionProducto a ResenaDto
    private ResenaDto mapearAResenaDto(CalificacionProducto calificacion) {
        ResenaDto dto = new ResenaDto();
        dto.setCalificacion(calificacion.getCalificacion());
        dto.setComentario(calificacion.getComentario());
        dto.setFecha(calificacion.getFecha());
        // Se asume que la relación Usuario se cargó
        dto.setNombreUsuario(calificacion.getUsuario().getNombre());
        return dto;
    }

    //Mapea Producto a ProductoDetalleDto.
    private ProductoDetalleDto mapearADetalleDto(Producto producto) {
        ProductoDetalleDto dto = new ProductoDetalleDto();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setImagenUrl(configuracionGlobalService.convertirUrlImagen(producto.getImagenUrl()));
        dto.setPrecio(producto.getPrecio());
        dto.setStock(producto.getStock());
        dto.setEsNuevo(producto.getEsNuevo());
        dto.setPromedioCalificaciones(producto.getPromedioCalificaciones());

        // Relaciones
        dto.setNombreVendedor(producto.getVendedor().getNombre());
        dto.setNombreCategoria(producto.getCategoria().getNombre());

        return dto;
    }






    /**
     * Crea o actualiza la calificación de un usuario para un producto.
     * @param idProducto ID del producto a calificar.
     * @param username Correo del usuario autenticado (reseñista).
     * @param dto Datos de la reseña (calificación y comentario).
     * @return La nueva (o actualizada) CalificacionProducto.
     */
    @Transactional
    public CalificacionProducto calificarProducto(Long idProducto, String username, ResenaRequestDto dto) {

        // 1. Obtener Entidades
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new NoSuchElementException("Producto no encontrado con ID: " + idProducto));

        Usuario usuario = usuarioServicio.obtenerUsuarioPorCorreo(username)
                .orElseThrow(() -> new NoSuchElementException("Usuario autenticado no encontrado: " + username));

        // 2. Verificar si ya existe una calificacion del usuario para este producto
        Optional<CalificacionProducto> calificacionExistente =
                calificacionProductoRepository.findByIdProductoAndUsuario(idProducto, usuario);

        // 3. Crear o Actualizar la Calificacion
        CalificacionProducto calificacion;
        if (calificacionExistente.isPresent()) {
            // Actualizar
            calificacion = calificacionExistente.get();
        } else {
            // Crear Nueva
            calificacion = new CalificacionProducto();
            calificacion.setProducto(producto);
            calificacion.setUsuario(usuario);
            // Configurar la clave compuesta
            calificacion.setId(new CalificacionProductoId(idProducto, usuario.getId()));
        }

        calificacion.setCalificacion(dto.getCalificacion());
        calificacion.setComentario(dto.getComentario());
        calificacion.setFecha(LocalDateTime.now()); // Actualiza la fecha

        CalificacionProducto calificacionGuardada = calificacionProductoRepository.save(calificacion);

        // 4. Recalcular y Actualizar el promedio de calificaciones del producto
        actualizarPromedioCalificaciones(producto);

        return calificacionGuardada;
    }

    //Metodo auxiliar para recalcular el promedio
    @Transactional
    private void actualizarPromedioCalificaciones(Producto producto) {
        // Obtenemos todas las calificaciones del producto
        List<CalificacionProducto> todasLasCalificaciones = calificacionProductoRepository.findByIdProducto(producto.getId());

        if (todasLasCalificaciones.isEmpty()) {
            producto.setPromedioCalificaciones(BigDecimal.ZERO);
            return;
        }

        // Calcular la suma total de las calificaciones
        double sumaCalificaciones = todasLasCalificaciones.stream()
                .mapToInt(CalificacionProducto::getCalificacion)
                .sum();

        // Calcular el nuevo promedio
        BigDecimal nuevoPromedio = BigDecimal.valueOf(sumaCalificaciones)
                .divide(BigDecimal.valueOf(todasLasCalificaciones.size()), 2, RoundingMode.HALF_UP);

        // Actualizar el producto en la base de datos
        producto.setPromedioCalificaciones(nuevoPromedio);
        productoRepository.save(producto);
    }


    /**
     * Obtiene todas las categorías disponibles en la base de datos.
     */
    public List<Categoria> obtenerCategorias() {
        return categoriaRepository.findAll();
    }

}
