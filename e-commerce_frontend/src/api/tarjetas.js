import api from '@/plugins/axios';

const API_URL = '/tarjetas';

/**
 * Obtiene la lista de tarjetas del usuario.
 * (Corresponde a GET /api/tarjetas)
 */
export const obtenerTarjetasUsuario = async () => {
    try {
        const response = await api.get(API_URL);
        return response.data;
    } catch (error) {
        throw error;
    }
};

/**
 * Agrega una nueva tarjeta al usuario.
 * (Corresponde a POST /api/tarjetas)
 */
export const crearTarjeta = async (tarjetaData) => {
    try {
        const response = await api.post(API_URL, tarjetaData);
        return response.data;
    } catch (error) {
        throw error;
    }
};

/**
 * Elimina una tarjeta específica.
 * (Corresponde a DELETE /api/tarjetas/{id})
 */
export const eliminarTarjeta = async (tarjetaId) => {
    try {
        // Devuelve 204 No Content en caso de éxito
        await api.delete(`${API_URL}/${tarjetaId}`);
        return true;
    } catch (error) {
        throw error;
    }
};