package com.jbrod.ecommerce_api.controladores;

import com.jbrod.ecommerce_api.dto.ProductoCreacionDTO;
import com.jbrod.ecommerce_api.modelos.productos.Producto;
import com.jbrod.ecommerce_api.servicios.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
// IMPORTACIÓN ELIMINADA: import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Controlador para la gestión de Productos (CRUD básico para vendedores y consultas).
 */
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoServicio;

    public ProductoController(ProductoService productoServicio) {
        this.productoServicio = productoServicio;
    }

    /**
     * Endpoint para que un vendedor registre un nuevo producto.
     * La petición ahora DEBE ser de tipo application/json, incluyendo la imagen codificada en Base64.
     * URL: POST /api/productos
     * @param dto Datos del producto a crear, incluyendo la imagen Base64 (recibida con @RequestBody).
     * @param authentication Objeto de autenticación para obtener el usuario autenticado.
     * @return El producto creado con estado "pendiente".
     */
    @PostMapping
    public ResponseEntity<Producto> crearProducto(

            @Valid @RequestBody ProductoCreacionDTO dto,

            Authentication authentication) {

        // El correo del vendedor es su principal de autenticación
        String correoVendedor = authentication.getName();

        try {
            // Llama al servicio. El servicio ahora debe manejar la decodificación del Base64
            // (dto.getImagenBase64()) y el guardado en DB.
            // Necesitarás actualizar la firma de tu servicio para que ya no reciba MultipartFile.

            // ASUMO que tu servicio necesita ser corregido, aquí llamamos a un nuevo método:
            Producto nuevoProducto = productoServicio.crearProductoBase64(dto, correoVendedor);
            return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
        } catch (IOException e) {
            // Error en la subida del archivo (ej. fallo al escribir en disco)
            System.err.println("Error al decodificar o guardar imagen Base64: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // 400 Bad Request
        } catch (NoSuchElementException e) {
            // Error de lógica (ej. Vendedor o Categoría no encontrados)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 Not Found
        } catch (IllegalStateException e) {
            // Error de configuración (ej. Estado 'pendiente' no existe en DB)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500
        } catch (IllegalArgumentException e) {
            // Captura si el Base64 no es válido
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }


    /**
     * NUEVO ENDPOINT: Obtiene la lista de productos disponibles en el Marketplace.
     * Corresponde a la llamada GET /api/productos que espera IndexView.vue.
     * Es accesible para todos los usuarios autenticados.
     * URL: GET /api/productos
     * @return Lista de Productos APROBADOS y con stock > 0.
     */
    @GetMapping
    public ResponseEntity<List<Producto>> obtenerCatalogoProductos() {
        try {
            List<Producto> productos = productoServicio.obtenerProductosMarketplace();
            return ResponseEntity.ok(productos);
        } catch (IllegalStateException e) {
            // Este catch maneja si el estado 'aprobado' no existe en la DB (problema de configuración)
            System.err.println("Error de configuración de estados: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    /**
     * Endpoint protegido para que el usuario autenticado (vendedor/cliente) vea sus productos.
     * Solo accesible si el usuario tiene un token JWT válido.
     * URL: GET /api/productos/inventario
     * @param authentication Objeto de autenticación de Spring Security.
     * @return Lista de productos del usuario autenticado.
     */
    @GetMapping("/inventario")
    public ResponseEntity<List<Producto>> obtenerInventarioVendedor(Authentication authentication) {
        try {
            String correoUsuario = authentication.getName();

            // Llama al servicio para obtener la lista de productos de ese usuario
            List<Producto> productos = productoServicio.obtenerProductosPorVendedor(correoUsuario);

            return ResponseEntity.ok(productos);
        } catch (NoSuchElementException e) {
            // Si el usuario autenticado no existe en la base de datos (muy improbable), devolvemos 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }





}
