import api from '@/plugins/axios';


const API_URL = '/productos'; 

/**
 * Obtiene el detalle completo de un producto específico (GET /api/productos/{id}).
 */
export const obtenerDetalleProducto = async (productoId) => {
    try {
        const response = await api.get(`${API_URL}/${productoId}`);
        return response.data; // Devuelve ProductoDetalleDto
    } catch (error) {
        throw error;
    }
};

/**
 * Registra o actualiza una reseña para un producto (POST /api/productos/{id}/resena).
 */
export const enviarResena = async (productoId, resenaData) => {
    try {
        const response = await api.post(`${API_URL}/${productoId}/resena`, resenaData);
        return response.data; 
    } catch (error) {
        throw error;
    }
};