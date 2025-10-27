<template>
    <div class="container py-4">
        <h1 class="mb-5 border-bottom pb-2 text-secondary">Mi Carrito de Compras</h1>

        <div v-if="carritoStore.cargando" class="alert alert-info text-center">
            <span class="spinner-border spinner-border-sm me-2"></span> Cargando carrito...
        </div>
        <div v-if="carritoStore.error" class="alert alert-danger">{{ carritoStore.error }}</div>

        <div v-if="!carritoStore.cargando && carritoStore.cantidadProductos === 0 && !carritoStore.error" class="alert alert-warning text-center">
            <i class="bi bi-cart-x me-2"></i> Tu carrito está vacío. ¡Explora el <RouterLink :to="{ name: 'vendedor-index' }" class="alert-link">Marketplace</RouterLink>!
        </div>

        <div v-else-if="carritoStore.carrito">
            <div class="row">
                <div class="col-md-8">
                    <div class="list-group">
                        <div 
                            v-for="item in carritoStore.carrito.items" 
                            :key="item.productoId" 
                            class="list-group-item d-flex align-items-center mb-3 p-3 shadow-sm rounded-3"
                        >

                            <img 
                                v-ngrok-img="item.imagenUrl" 
                                alt="Producto" 
                                class="rounded me-3" 
                                style="width: 80px; height: 80px; object-fit: cover;"
                                onerror="this.onerror=null; this.src='https://placehold.co/80x80?text=Prod';"
                            >
                            <div class="flex-grow-1">
                                <h5 class="mb-1">{{ item.nombreProducto }}</h5>
                                <p class="mb-1 text-muted small">Precio: Q{{ item.precioUnitario.toFixed(2) }}</p>
                                <p class="mb-0 fw-bold">Subtotal: Q{{ item.subtotal.toFixed(2) }}</p>
                            </div>

                            <div class="d-flex align-items-center mx-4">
                                <label for="cantidad" class="form-label me-2 mb-0 small">Cant:</label>
                                <input 
                                    type="number" 
                                    min="1" 
                                    :value="item.cantidad" 
                                    @change="manejarCambioCantidad(item.productoId, $event.target.value)"
                                    class="form-control form-control-sm text-center" 
                                    style="width: 70px;"
                                    :disabled="carritoStore.cargando"
                                >
                            </div>

                            <button 
                                @click="manejarEliminarProducto(item.productoId)"
                                class="btn btn-outline-danger btn-sm ms-3"
                                :disabled="carritoStore.cargando"
                            >
                                <i class="bi bi-trash"></i>
                            </button>
                        </div>
                    </div>
                </div>

                <div class="col-md-4">
                    <div class="card shadow-sm sticky-top" style="top: 20px;">
                        <div class="card-body">
                            <h4 class="card-title mb-3 border-bottom pb-2">Resumen del Pedido</h4>
                            <div class="d-flex justify-content-between mb-2">
                                <span class="text-muted">Ítems ({{ carritoStore.cantidadProductos }} tipos)</span>
                                <span class="fw-bold">Q{{ carritoStore.montoTotal.toFixed(2) }}</span>
                            </div>
                            <div class="d-flex justify-content-between fw-bold fs-5 mt-3 pt-3 border-top">
                                <span>Total a Pagar:</span>
                                <span>Q{{ carritoStore.montoTotal.toFixed(2) }}</span>
                            </div>

                            <RouterLink 
                                :to="{ name: 'pago' }" 
                                class="btn btn-success w-100 mt-4 rounded-pill"
                                :disabled="carritoStore.montoTotal === 0"
                            >
                                <i class="bi bi-credit-card me-2"></i> Proceder al Pago
                            </RouterLink>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { onMounted } from 'vue';
import { useCarritoStore } from '@/stores/carrito';

const carritoStore = useCarritoStore();

/**
 * Llama a la acción para actualizar la cantidad del producto en el carrito.
 */
const manejarCambioCantidad = (idProducto, nuevaCantidadStr) => {
    const nuevaCantidad = parseInt(nuevaCantidadStr);

    if (isNaN(nuevaCantidad) || nuevaCantidad < 1) {
        console.error("Cantidad inválida");
        return;
    }
    
    // Llamar a la misma acción de agregar/actualizar
    carritoStore.agregarOActualizarProducto(idProducto, nuevaCantidad)
        .catch(err => {
            // Manejar errores de stock si el backend lo reporta
            console.error("Error al actualizar cantidad:", err);
            // Podrías recargar el carrito para restaurar el valor correcto
            carritoStore.cargarCarrito(); 
        });
};

/**
 * Llama a la acción para eliminar el producto.
 */
const manejarEliminarProducto = (idProducto) => {
    if (confirm('¿Estás seguro de que quieres eliminar este producto del carrito?')) {
        carritoStore.eliminarProducto(idProducto)
            .catch(err => {
                 console.error("Error al eliminar producto:", err);
            });
    }
};

onMounted(() => {
    // Asegurarse de que el carrito esté cargado al entrar a la vista
    carritoStore.cargarCarrito();
});
</script>

<style scoped>
/* Estilos adicionales si son necesarios */
.sticky-top {
    top: 20px; /* Separación del borde superior */
}
</style>