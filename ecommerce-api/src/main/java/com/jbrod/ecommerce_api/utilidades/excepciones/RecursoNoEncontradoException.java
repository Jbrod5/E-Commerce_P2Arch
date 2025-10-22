package com.jbrod.ecommerce_api.utilidades.excepciones;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción personalizada para manejar la no existencia de un recursoooo
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class RecursoNoEncontradoException extends RuntimeException {

    private static final long serialVersionUID = 1L; // Recomendado para Serializable

    /**
     * Constructor que acepta un mensaje detallado.
     * @param message Mensaje que describe el recurso no encontrado.
     */
    public RecursoNoEncontradoException(String message) {
        super(message);
    }

    /**
     * Constructor conveniente para generar un mensaje estándar.
     * @param nombreRecurso El tipo de recurso que no se encontró (ej. "Producto").
     * @param idRecurso El identificador del recurso (ej. el ID).
     */
    public RecursoNoEncontradoException(String nombreRecurso, Long idRecurso) {
        super(String.format("%s no encontrado con ID : '%s'", nombreRecurso, idRecurso));
    }

    // Si necesitas un constructor para incluir el error raíz (throwable)
    public RecursoNoEncontradoException(String message, Throwable cause) {
        super(message, cause);
    }
}