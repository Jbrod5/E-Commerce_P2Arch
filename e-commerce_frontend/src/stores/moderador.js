// src/stores/moderador.js
import { defineStore } from 'pinia';
import { ref } from 'vue';
import ModeradorAPI from '@/api/ModeradorApi';

export const useModeradorStore = defineStore('moderador', () => {
    // Estado
    const cantidadPendientes = ref(0);

    // Acciones
    /**
     * Carga la cantidad actual de solicitudes de venta pendientes.
     */
    const cargarConteoPendientes = async () => {
        try {
            // Nota: El API.obtenerSolicitudesPendientes() trae el listado completo, 
            // la cantidad es simplemente el tamaño del array.
            const response = await ModeradorAPI.obtenerSolicitudesPendientes();
            cantidadPendientes.value = response.data.length;
        } catch (error) {
            // Es común que falle si el backend aún no está listo o el usuario no es moderador
            console.error('Error al cargar conteo de solicitudes pendientes:', error);
            cantidadPendientes.value = 0;
        }
    };

    /**
     * Decrementa el contador cuando una solicitud es aprobada o rechazada.
     */
    const decrementarPendientes = () => {
        if (cantidadPendientes.value > 0) {
            cantidadPendientes.value--;
        }
    };

    /**
     * Establece manualmente el conteo (útil al cargar la vista principal).
     */
    const setCantidadPendientes = (cantidad) => {
        cantidadPendientes.value = cantidad;
    };

    return {
        cantidadPendientes,
        cargarConteoPendientes,
        decrementarPendientes,
        setCantidadPendientes
    };
});
