import api from '@/plugins/axios.js';

const REPORTES_URL = '/reportes';

export default {
    /**
     * Obtiene el Top 10 de productos más vendidos en un intervalo de tiempo.
     * @param {string} fechaInicio - Fecha de inicio en formato ISO 8601 (YYYY-MM-DDTHH:mm:ss).
     * @param {string} fechaFin - Fecha de fin en formato ISO 8601 (YYYY-MM-DDTHH:mm:ss).
     * @returns {Promise<Array<{nombreProducto: string, totalVendido: number}>>}
     */
    obtenerTop10ProductosVendidos(fechaInicio, fechaFin) {
        return api.get(`${REPORTES_URL}/top-productos-vendidos`, {
            params: {
                fechaInicio: fechaInicio,
                fechaFin: fechaFin
            }
        });
    },

    /**
     * Obtiene el Top 5 de clientes compradores por monto total gastado.
     * @returns {Promise<Array<{nombreCliente: string, montoTotalCompras: number, gananciaPlataforma: number}>>}
     */
    obtenerTop5ClientesCompradores(fechaInicio, fechaFin) {
        return api.get(`${REPORTES_URL}/top-clientes-compradores`, {
            params: {
                fechaInicio: fechaInicio,
                fechaFin: fechaFin
            }
        });
    },


    /**
     * Obtiene el Top 5 de vendedores por unidades vendidas.
     * @returns {Promise<Array<{nombreVendedor: string, totalUnidadesVendidas: number}>>}
     */
    obtenerTop5Vendedores(fechaInicio, fechaFin) { 
        return api.get(`${REPORTES_URL}/top-vendedores`, {
            params: {
                fechaInicio: fechaInicio,
                fechaFin: fechaFin
            }
        });
    },


    /**
     * Obtiene el Top 10 de clientes compradores por número de pedidos.
     */
    obtenerTop10ClientesMasPedidos(fechaInicio, fechaFin) {
        return api.get(`${REPORTES_URL}/top-clientes-pedidos`, {
            params: {
                fechaInicio: fechaInicio,
                fechaFin: fechaFin
            }
        });
    },


    /**
     * Obtiene el Top 10 de vendedores por número de productos listados (actuales).
     */
    obtenerTop10VendedoresConMasProductos() {
        return api.get(`${REPORTES_URL}/top-vendedores-productos`);

    },

    /**
     * Obtiene el historial completo de suspensiones (sanciones).
     */
    obtenerHistorialSuspensiones() {
        return api.get(`${REPORTES_URL}/historial-sanciones`);

    },

    /**
     * Obtiene el historial completo de notificaciones.
     */
    obtenerHistorialNotificaciones() {
        return api.get(`${REPORTES_URL}/historial-notificaciones`);

    },

};