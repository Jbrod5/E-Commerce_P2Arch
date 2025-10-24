import api from '@/plugins/axios'; 

const API_URL = '/pedidos'; 

/**
 * Llama al endpoint de checkout para finalizar la compra.
 */
export const finalizarCompra = async (checkoutData) => {
    try {
        const response = await api.post(`${API_URL}/checkout`, checkoutData);
        return response.data;
    } catch (error) {
        throw error;
    }
};

/**
 * Obtiene el resumen de todos los pedidos del usuario (GET /api/pedidos).
 */
export const obtenerMisPedidos = async () => {
    try {
        const response = await api.get(API_URL);
        return response.data; // Devuelve List<PedidoResumenDto>
    } catch (error) {
        throw error;
    }
};

/**
 * Obtiene el detalle completo de un pedido específico (GET /api/pedidos/{id}).
 */
export const obtenerDetallePedido = async (pedidoId) => {
    try {
        const response = await api.get(`${API_URL}/${pedidoId}`);
        return response.data; // Devuelve PedidoDetalleDto
    } catch (error) {
        throw error;
    }
};