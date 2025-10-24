import api from '@/plugins/axios'; 

const API_URL = '/pedidos'; // Se convierte en http://localhost:8080/api/pedidos

/**
 * Llama al endpoint de checkout para finalizar la compra.
 * @param {object} checkoutData - { tarjetaId: Long, direccion: string }
 * @returns {Promise<object>} El objeto PedidoResponseDto (idPedido, montoTotal, etc.).
 */
export const finalizarCompra = async (checkoutData) => {
    try {
        const response = await api.post(`${API_URL}/checkout`, checkoutData);
        return response.data;
    } catch (error) {
        // Dejamos que el interceptor maneje el 401 y lanzamos el error para que la vista lo gestione (400, 404, 500)
        throw error;
    }
};

