import axios from '@/plugins/axios'

const BASE_URL = '/moderador/productos';

/**
 * Módulo de funciones para interactuar con los endpoints del Moderador.
 */
const ModeradorAPI = {

    /**
     * Obtiene todas las solicitudes de productos que están en estado 'pendiente'.
     * GET /api/moderador/productos/pendientes
     * @returns {Promise<axios.Response>} Lista de SolicitudPendienteDto.
     */
    obtenerSolicitudesPendientes() {
        return axios.get(`${BASE_URL}/pendientes`);
    },

    /**
     * Envía la decisión del moderador (aprobación o rechazo) a un producto.
     * POST /api/moderador/productos/{idProducto}/revisar
     *
     * @param {number} idProducto - ID del producto a revisar.
     * @param {boolean} aprobado - True si se aprueba, False si se rechaza.
     * @param {string} [comentario] - Comentario del moderador (requerido para rechazo).
     * @returns {Promise<axios.Response>} El Producto actualizado.
     */
    revisarProducto(idProducto, aprobado, comentario = null) {
        // Construye el DecisionModeracionDto
        const dto = {
            aprobado: aprobado,
            comentario: comentario // Spring Boot recibirá null si no se proporciona
        };

        return axios.post(`${BASE_URL}/${idProducto}/revisar`, dto);
    },

};

export default ModeradorAPI;
