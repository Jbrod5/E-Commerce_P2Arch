<template>
  <div class="checkout-container container mt-5">
    <h2>Finalizar Compra</h2>
    
    <div v-if="!carritoStore.cargando && carritoStore.cantidadProductos === 0 && !carritoStore.ultimoPedido">
        <div class="alert alert-warning text-center">
            El carrito está vacío. <router-link :to="{ name: 'carrito' }">Ir al carrito</router-link>.
        </div>
    </div>

    <div v-else-if="carritoStore.ultimoPedido" class="confirmacion-card card p-4 shadow-sm">
        <h3 class="text-success text-center mb-4">¡Pedido Realizado con Éxito!</h3>
        <p><strong>Pedido #ID:</strong> {{ carritoStore.ultimoPedido.idPedido }}</p>
        <p><strong>Monto Total:</strong> {{ formatCurrency(carritoStore.ultimoPedido.montoTotal) }}</p>
        <p><strong>Dirección de Envío:</strong> {{ carritoStore.ultimoPedido.direccion }}</p>
        <p><strong>Entrega Estimada:</strong> {{ formatDate(carritoStore.ultimoPedido.fechaEntregaEstimada) }}</p>
        <hr>
        <router-link :to="{ name: 'vendedor-index' }" class="btn btn-primary mt-3">
            Volver al Marketplace
        </router-link>
    </div>

    <div v-else class="row">
        <div class="col-md-7">
            <div class="card p-4 shadow-sm">
                <form @submit.prevent="handleCheckout">
                    <h4 class="mb-3">Información de Pago y Envío</h4>
                    
                    <div class="form-group mb-3">
                        <label for="tarjeta" class="form-label">Selecciona Tarjeta</label>
                        <select id="tarjeta" v-model="tarjetaSeleccionadaId" class="form-select" required>
                            <option value="" disabled>-- Selecciona una tarjeta --</option>
                            <option v-for="tarjeta in tarjetasDisponibles" :key="tarjeta.id" :value="tarjeta.id">
                                **** **** **** {{ tarjeta.parteVisible }} ({{ tarjeta.titular }})
                            </option>
                        </select>
                        <p v-if="tarjetasDisponibles.length === 0" class="text-info mt-2">
                             Puedes <router-link :to="{ name: 'gestionTarjetas' }">administrar tus tarjetas aquí</router-link> para guardarlas.
                        </p>
                    </div>

                    <div class="form-group mb-4">
                        <label for="direccion" class="form-label">Dirección de Envío Completa</label>
                        <textarea 
                            id="direccion" 
                            v-model="direccionEnvio" 
                            rows="3" 
                            class="form-control"
                            placeholder="Calle, número, colonia, código postal, etc."
                            required
                        ></textarea>
                    </div>

                    <div v-if="carritoStore.error" class="alert alert-danger">{{ carritoStore.error }}</div>
                    
                    <button 
                        type="submit" 
                        :disabled="carritoStore.cargando || !tarjetaSeleccionadaId || !direccionEnvio" 
                        class="btn btn-success w-100 btn-lg"
                    >
                        <span v-if="carritoStore.cargando" class="spinner-border spinner-border-sm me-2"></span>
                        Pagar {{ formatCurrency(carritoStore.montoTotal) }}
                    </button>
                </form>
            </div>
        </div>

        <div class="col-md-5">
            <div class="card p-4 resumen-card shadow-sm">
                <h4 class="mb-3">Resumen de la Orden</h4>
                <div class="table-responsive mb-3">
                    <table class="table table-sm table-borderless">
                        <thead class="border-bottom">
                            <tr>
                                <th>Artículo</th>
                                <th class="text-end">Unidades</th>
                                <th class="text-end">Subtotal</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="item in carritoStore.carrito?.items" :key="item.productoId">
                                <td>
                                    {{ item.nombreProducto }}
                                    <div class="text-muted small">{{ formatCurrency(item.precioUnitario) }} c/u</div>
                                </td>
                                <td class="text-end">{{ item.cantidad }}</td>
                                <td class="text-end fw-bold">{{ formatCurrency(item.subtotal) }}</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <div class="d-flex justify-content-between border-top pt-2">
                    <h5>Total:</h5>
                    <h5><strong>{{ formatCurrency(carritoStore.montoTotal) }}</strong></h5>
                </div>
            </div>
        </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useCarritoStore } from '@/stores/carrito';
//  Ahora usamos el módulo correcto: tarjetas.js
import { obtenerTarjetasUsuario } from '@/api/tarjetas'; 
import { useRouter } from 'vue-router';

const carritoStore = useCarritoStore();
const router = useRouter();

// Datos del Formulario
const tarjetaSeleccionadaId = ref('');
const direccionEnvio = ref('');
const tarjetasDisponibles = ref([]);

// --- Utilidades ---
const formatCurrency = (amount) => {
    const num = parseFloat(amount); 
    if (isNaN(num)) return 'Q0.00';
    return new Intl.NumberFormat('es-GT', { style: 'currency', currency: 'GTQ' }).format(num);
};

const formatDate = (dateString) => {
    // Verifica si la fecha es válida
    const date = new Date(dateString);
    if (isNaN(date)) return 'N/A';
    return date.toLocaleDateString('es-GT', { year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' });
};

// --- Lógica de Carga y Checkout ---
const loadData = async () => {
    carritoStore.error = null;
    
    // 1. Cargar Carrito (Si no está cargado)
    if (!carritoStore.carrito || carritoStore.cantidadProductos === 0) {
        try {
            await carritoStore.cargarCarrito();
        } catch (error) {
            console.error("Error al cargar carrito en PagoView:", error);
        }
    }
    
    // 2. Cargar Tarjetas
    try {
        // Llamada al nuevo módulo API de tarjetas
        tarjetasDisponibles.value = await obtenerTarjetasUsuario();
        if (tarjetasDisponibles.value.length > 0) {
            // Seleccionar la primera por defecto
            tarjetaSeleccionadaId.value = tarjetasDisponibles.value[0].id;
        }
    } catch (error) {
        console.error("No se pudieron cargar las tarjetas.");
        // Si falla, el arreglo queda vacío y se muestra el mensaje de ayuda.
    }

    // 3. Verificación de Carrito
    if (carritoStore.cantidadProductos === 0 && !carritoStore.ultimoPedido) {
        // Si el carrito está vacío y no hay pedido reciente, redirigir
        setTimeout(() => {
            if (router.currentRoute.value.name === 'pago') { 
                router.push({ name: 'vendedor-index' });
            }
        }, 2000); 
    }
};

const handleCheckout = async () => {
    const checkoutData = {
        tarjetaId: tarjetaSeleccionadaId.value,
        direccion: direccionEnvio.value.trim(),
    };

    try {
        await carritoStore.procesarCheckout(checkoutData);
    } catch (error) {
        console.error("Error al procesar el pago:", error);
    }
};

onMounted(() => {
    loadData();
    if (carritoStore.ultimoPedido) {
        carritoStore.ultimoPedido = null;
    }
});
</script>

<style scoped>
.checkout-container {
    padding-bottom: 50px;
}

.resumen-card {
    background-color: #f8f9fa;
    border-left: 5px solid #007bff;
}

.confirmacion-card {
    max-width: 500px;
    margin: 50px auto;
    border-left: 5px solid #28a745;
}
</style>