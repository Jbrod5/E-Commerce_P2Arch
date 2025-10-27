// src/stores/carrito.js

import { defineStore } from 'pinia';
import api from '@/plugins/axios.js';


// Función API para el checkout
import { finalizarCompra } from '@/api/pedido'; 

const ENDPOINT_CARRITO = '/carrito'; 

export const useCarritoStore = defineStore('carrito', {
    state: () => ({
        // CarritoResponseDto del backend
        carrito: null, 
        cargando: false,
        error: null,
        ultimoPedido: null, 
    }),

    getters: {
        // Devuelve el número total de ítems distintos
        cantidadProductos: (state) => state.carrito?.items.length || 0,
        // Devuelve el monto total
        montoTotal: (state) => state.carrito?.montoTotal || 0,
    },

    actions: {
        /**
         * Carga el carrito activo del usuario (o lo crea si no existe).
         */
        async cargarCarrito() {
            this.cargando = true;
            this.error = null;
            try {
                // GET /api/carrito
                const response = await api.get(ENDPOINT_CARRITO);
                this.carrito = response.data;
                return this.carrito;
            } catch (err) {
                this.error = 'Error al cargar el carrito: ' + (err.response?.data || err.message);
                this.carrito = null;
                throw err;
            } finally {
                this.cargando = false;
            }
        },

        /**
         * Agrega un producto o actualiza la cantidad.
         * @param {number} idProducto El ID del producto
         * @param {number} cantidad La cantidad deseada
         */
        async agregarOActualizarProducto(idProducto, cantidad) {
            this.cargando = true;
            this.error = null;
            try {
                // POST /api/carrito
                const itemDto = { productoId: idProducto, cantidad };
                const response = await api.post(ENDPOINT_CARRITO, itemDto);
                
                this.carrito = response.data; // Carrito actualizado
                return true; 
            } catch (err) {
                const mensaje = err.response?.data || 'Error desconocido al agregar producto.';
                this.error = mensaje;
                throw mensaje;
            } finally {
                this.cargando = false;
            }
        },

        /**
         * Elimina un producto específico del carrito.
         * @param {number} idProducto ID del producto a eliminar
         */
        async eliminarProducto(idProducto) {
            this.cargando = true;
            this.error = null;
            try {
                // DELETE /api/carrito/{idProducto}
                const response = await api.delete(`${ENDPOINT_CARRITO}/${idProducto}`);
                this.carrito = response.data; // Carrito actualizado
                return true;
            } catch (err) {
                const mensaje = err.response?.data || 'Error al eliminar producto.';
                this.error = mensaje;
                throw mensaje;
            } finally {
                this.cargando = false;
            }
        },
        

        
        //  PROCESAR CHECKOUT / PEDIDO FINAL :33333
        /**
         * Llama al API para finalizar la compra (Checkout).
         * @param {object} checkoutData - { tarjetaId: Long, direccion: string }
         * @returns {object} PedidoResponseDto de la compra exitosa.
         */
        async procesarCheckout(checkoutData) {
            this.cargando = true;
            this.error = null;
            this.ultimoPedido = null;
            try {
                // 1. Llamar al módulo API para ejecutar la transacción en el backend
                const response = await finalizarCompra(checkoutData);
                
                // 2. Si es exitoso, actualizar el estado
                this.ultimoPedido = response; 
                
                // 3. Vaciar el carrito en el estado local (El backend ya lo vació en la DB)
                this.carrito = { items: [], montoTotal: 0 }; 
                
                return response;
            } catch (err) {
                // Capturar el mensaje de error del backend (stock insuficiente, tarjeta no válida, etc.)
                const mensaje = err.response?.data || 'Error desconocido al procesar el pago.';
                this.error = mensaje;
                throw mensaje; // Propagar el error para que la vista lo maneje
            } finally {
                this.cargando = false;
            }
        },
    }
});