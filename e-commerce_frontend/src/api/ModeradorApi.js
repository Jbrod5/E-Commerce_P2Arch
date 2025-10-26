import axios from '@/plugins/axios'

// La base ahora es '/moderador' para incluir productos y gestión de sanciones
const BASE_URL_MODERADOR = '/moderador';
const PRODUCTOS_URL = `${BASE_URL_MODERADOR}/productos`;

/**
 * Módulo de funciones para interactuar con los endpoints del Moderador.
 */
const ModeradorAPI = {

    // --- FUNCIONALIDADES DE GESTIÓN DE PRODUCTOS ---
    
    /**
     * Obtiene todas las solicitudes de productos que están en estado 'pendiente'.
     * GET /api/moderador/productos/pendientes
     * @returns {Promise<axios.Response>} Lista de SolicitudPendienteDto.
     */
    obtenerSolicitudesPendientes() {
        return axios.get(`${PRODUCTOS_URL}/pendientes`);
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
            comentario: comentario
        };

        return axios.post(`${PRODUCTOS_URL}/${idProducto}/revisar`, dto);
    },

    // --- GESTIÓN DE SANCIONES ---
    
    /**
     * Obtiene la lista de usuarios con rol 'comun' (vendedores).
     * GET /api/moderador/vendedores
     * @returns {Promise<axios.Response>} Lista de UsuarioVendedorDto.
     */
    obtenerUsuariosVendedores() {
        return axios.get(`${BASE_URL_MODERADOR}/vendedores`);
    },

    /**
     * Sanciona a un usuario común (vendedor) por un periodo de tiempo.
     * POST /api/moderador/sancionar
     *
     * @param {Object} suspensionData - Datos de la sanción.
     * @param {number} suspensionData.idUsuarioASuspender - ID del usuario a sancionar.
     * @param {string} suspensionData.motivo - Motivo de la sanción.
     * @param {string} suspensionData.fechaFin - Fecha y hora de finalización (formato ISO 8601).
     * @returns {Promise<axios.Response>} La Suspensión creada.
     */
    sancionarUsuario(suspensionData) {
        return axios.post(`${BASE_URL_MODERADOR}/sancionar`, suspensionData);
    },

    /**
     * Obtiene el historial de suspensiones para un usuario específico.
     * GET /api/moderador/sanciones/historial/{idUsuario}
     *
     * @param {number} idUsuario - ID del usuario a consultar.
     * @returns {Promise<axios.Response>} Lista de SuspensionDTO.
     */
    obtenerHistorialSanciones(idUsuario) {
        return axios.get(`${BASE_URL_MODERADOR}/sanciones/historial/${idUsuario}`);
    },
};

export default ModeradorAPI;
