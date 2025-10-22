<template>
    <div class="container py-4">
        <h1 class="mb-5 border-bottom pb-2 text-primary">Proceso de Pago y Checkout 💳</h1>
        
        <div v-if="carritoStore.cargando || carritoStore.montoTotal === 0" class="alert alert-warning text-center">
            <i class="bi bi-info-circle me-2"></i> Redirigiendo...
        </div>

        <div v-else class="row">
            <div class="col-md-8">
                <h4 class="mb-4">1. Dirección de Envío y Contacto</h4>
                <div class="card p-4 mb-4 shadow-sm">
                    <p class="text-muted">⚠️ Implementar formulario de dirección y selección de método de envío aquí.</p>
                </div>

                <h4 class="mb-4">2. Método de Pago</h4>
                <div class="card p-4 shadow-sm">
                    <p class="text-muted">⚠️ Implementar formulario de tarjeta de crédito/débito o selección de tarjeta guardada.</p>
                </div>
            </div>

            <div class="col-md-4">
                <div class="card bg-light shadow-sm sticky-top" style="top: 20px;">
                    <div class="card-body">
                        <h4 class="card-title mb-3 border-bottom pb-2">Total del Pedido</h4>
                        <div v-for="item in carritoStore.carrito.items" :key="item.productoId" class="d-flex justify-content-between small mb-1">
                            <span class="text-truncate">{{ item.cantidad }} x {{ item.nombreProducto }}</span>
                            <span>Q{{ item.subtotal.toFixed(2) }}</span>
                        </div>

                        <div class="d-flex justify-content-between fw-bold fs-5 mt-3 pt-3 border-top border-dark">
                            <span>TOTAL:</span>
                            <span>Q{{ carritoStore.montoTotal.toFixed(2) }}</span>
                        </div>

                        <button 
                            @click="manejarProcesarPago"
                            class="btn btn-lg btn-success w-100 mt-4"
                            :disabled="cargandoPago || !metodosDePagoListos"
                        >
                            <span v-if="cargandoPago" class="spinner-border spinner-border-sm me-2"></span>
                            Confirmar y Pagar
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useCarritoStore } from '@/stores/carrito';
import { useRouter } from 'vue-router'; // Para la redirección después del pago

const carritoStore = useCarritoStore();
const router = useRouter();

const cargandoPago = ref(false);
const metodosDePagoListos = ref(false); // Simulación de validación de formularios

/**
 * Lógica que se ejecutará al hacer clic en "Confirmar y Pagar".
 * Esto requiere implementar la acción 'procesarPago' en el store.
 */
const manejarProcesarPago = async () => {
    // 1. Validar la dirección y el pago aquí en el frontend
    if (!metodosDePagoListos.value) {
        alert("Por favor, completa la información de envío y pago.");
        return;
    }
    
    cargandoPago.value = true;
    try {
        // 🚨 Acción a implementar en el store: Llamar al endpoint POST /api/carrito/checkout
        // await carritoStore.procesarPago(/* datos de pago y envío */);
        
        alert("¡Pago exitoso! Serás redirigido al resumen de tu pedido.");
        
        // 2. Redirigir al usuario (ej. a una vista de "Pedidos")
        // router.push({ name: 'pedidos' });
        
    } catch (error) {
        alert(`Error al procesar el pago: ${error}`);
    } finally {
        cargandoPago.value = false;
    }
};

onMounted(() => {
    // Si el carrito está vacío o no cargó, redirigir a la vista de carrito
    if (carritoStore.montoTotal === 0 && !carritoStore.cargando) {
        router.push({ name: 'carrito' });
    }
    
    // Simulación de que los métodos de pago están listos
    metodosDePagoListos.value = true;
});
</script>

<style scoped>
/* Estilos adicionales si son necesarios */
.sticky-top {
    top: 20px; /* Separación del borde superior */
}
</style>