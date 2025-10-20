package com.jbrod.ecommerce_api.servicios;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Servicio que encapsula la lógica de almacenamiento de archivos.
 * Para el desarrollo local, guarda físicamente en un directorio del proyecto.
 * En producción, esto se reemplazaría por una implementación de AWS S3 o Cloudinary.
 */
@Service // Siguiendo la nomenclatura: AlmacenamientoArchivos + Service
public class AlmacenamientoArchivosService {

    // Directorio base donde se guardarán las imágenes (debe coincidir con WebConfig + subdirectorio)
    // El subdirectorio 'productos' ayuda a organizar las imágenes.
    private static final String UPLOAD_DIR = "src/main/resources/static/imagenes/productos";

    // URL base que Spring mapeará para acceder al archivo
    private static final String BASE_URL_MOCK = "http://localhost:8080/imagenes/productos/";

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
        return BASE_URL_MOCK + uniqueFilename;
    }
}
