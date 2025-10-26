package com.jbrod.ecommerce_api.servicios;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.Base64; // Importación necesaria para el Base64, aunque no se usa directamente aquí, es buena práctica.

/**
 * Servicio que encapsula la lógica de almacenamiento de archivos.
 * Para el desarrollo local, guarda físicamente en un directorio del proyecto.
 * En producción, esto se reemplazaría por una implementación de AWS S3 o Cloudinary.
 */
@Service // Siguiendo la nomenclatura: AlmacenamientoArchivos + Service
public class AlmacenamientoArchivosService {

    // Directorio base donde se guardarán las imágenes (debe coincidir con WebConfig + subdirectorio)
    // El subdirectorio 'productos' ayuda a organizar las imágenes.
    //private static final String UPLOAD_DIR = "src/main/resources/static/imagenes/productos";
    private static final String UPLOAD_DIR = "uploads/productos"; // ruta física


    // URL base que Spring mapeará para acceder al archivo
    private static final String BASE_URL = "http://localhost:8080/imagenes/productos/";


    private static final String RUTA_RELATIVA_BASE = "/imagenes/productos/";

    /**
     * Sube el archivo físicamente a la carpeta local y genera una URL de acceso.
     * @param file El archivo MultipartFile recibido de la petición.
     * @return La URL pública donde se accedería a la imagen.
     * @throws IOException Si la subida falla.
     */
    public String uploadFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("No se puede subir un archivo vacío.");
        }

        // 1. Asegurar que la carpeta de destino exista
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            // Crea la ruta completa si no existe
            Files.createDirectories(uploadPath);
        }

        // 2. Generar un nombre de archivo único con su extensión
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        // Prefijo 'prod-' para distinguir imágenes de productos de otras subidas futuras
        String uniqueFilename = "prod-" + UUID.randomUUID().toString() + extension;

        // 3. Crear el path final y guardar el archivo
        Path filePath = uploadPath.resolve(uniqueFilename);

        // Transferir el contenido del archivo subido al disco
        file.transferTo(filePath.toFile());

        // 4. Devolver la URL de acceso público
        return RUTA_RELATIVA_BASE + uniqueFilename;
    }

    /**
     * ***************************************************************
     * NUEVO MÉTODO: Sube un arreglo de bytes (imagen decodificada de Base64)
     * ***************************************************************
     * Guarda el array de bytes físicamente y genera una URL de acceso.
     * Dado que Base64 no incluye la extensión, asumimos una extensión común (.png).
     * @param bytes El array de bytes de la imagen decodificada.
     * @return La URL pública donde se accedería a la imagen.
     * @throws IOException Si la subida falla.
     */
    public String uploadBytes(byte[] bytes) throws IOException {
        if (bytes == null || bytes.length == 0) {
            throw new IOException("No se puede subir un array de bytes vacío.");
        }

        // 1. Asegurar que la carpeta de destino exista
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 2. Generar un nombre de archivo único y ASUMIR una extensión.
        // Esto no detecta el tipo de archivos exactamente xd, tendría que
        // examinar los primeros bytes del array (magic number) o requerir el mimetype
        // en el DTO del producto. Asumimos .png por simplicidad.
        String extension = ".png"; // Asumimos PNG o  usar .jpg??
        String uniqueFilename = "prod-" + UUID.randomUUID().toString() + extension;

        // 3. Crear el path final y guardar el archivo
        Path filePath = uploadPath.resolve(uniqueFilename);

        // Escribir el contenido binario (los bytes) directamente al disco
        Files.write(filePath, bytes);

        // 4. Devolver la URL de acceso público
        //return RUTA_RELATIVA_BASE + uniqueFilename;
        return BASE_URL + uniqueFilename;
    }
}
