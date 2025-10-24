<template>
  <div class="container py-5">
    <h1 class="mb-5">Gestión de Tarjetas Guardadas</h1>

    <div class="row">
      <div class="col-md-7">
        <div class="card shadow-sm p-3 mb-4">
            <h4 class="mb-3">Tus Tarjetas ({{ tarjetas.length }})</h4>
            
            <div v-if="isLoading && tarjetas.length === 0" class="text-center p-5">
                <div class="spinner-border text-primary" role="status"></div>
                <p class="mt-2">Cargando tarjetas...</p>
            </div>

            <div v-else-if="tarjetas.length === 0" class="alert alert-info text-center">
                Aún no tienes tarjetas registradas. ¡Agrega una para pagar más rápido!
            </div>

            <ul v-else class="list-group list-group-flush">
                <li v-for="tarjeta in tarjetas" :key="tarjeta.id" class="list-group-item d-flex justify-content-between align-items-center">
                    <div>
                        <i class="bi bi-credit-card-2-front-fill me-2 text-primary"></i>
                        <span class="fw-bold me-2">**** **** **** {{ tarjeta.parteVisible }}</span>
                        <small class="text-muted">Exp: {{ tarjeta.mesVencimiento }}/{{ tarjeta.anioVencimiento }}</small>
                        <p class="mb-0 mt-1"><small>Titular: {{ tarjeta.titular }}</small></p>
                    </div>
                    <div>
                        <button 
                            @click="confirmarEliminacion(tarjeta)" 
                            class="btn btn-sm btn-outline-danger"
                            :disabled="isDeleting"
                        >
                            <span v-if="isDeleting" class="spinner-border spinner-border-sm me-1"></span>
                            Eliminar
                        </button>
                    </div>
                </li>
            </ul>
        </div>
      </div>

      <div class="col-md-5">
        <div class="card shadow-sm p-4 sticky-top form-card">
            <h4 class="mb-4">Agregar Nueva Tarjeta</h4>
            
            <div v-if="errorFormulario" class="alert alert-danger">{{ errorFormulario }}</div>
            <div v-if="successFormulario" class="alert alert-success">{{ successFormulario }}</div>
            
            <form @submit.prevent="handleCrearTarjeta">
                <div class="mb-3">
                    <label for="numeroTarjeta" class="form-label">Número de Tarjeta (13-16 dígitos)</label>
                    <input 
                        type="text" 
                        class="form-control" 
                        id="numeroTarjeta" 
                        v-model="nuevaTarjeta.numeroTarjeta" 
                        pattern="[0-9]{13,16}"
                        title="Solo números (13 a 16 dígitos)"
                        required
                        :disabled="isCreating"
                    />
                </div>
                
                <div class="mb-3">
                    <label for="titular" class="form-label">Nombre del Titular</label>
                    <input 
                        type="text" 
                        class="form-control" 
                        id="titular" 
                        v-model="nuevaTarjeta.titular" 
                        required
                        :disabled="isCreating"
                    />
                </div>
                
                <div class="row mb-4">
                    <div class="col-6">
                        <label for="mesVencimiento" class="form-label">Mes Exp.</label>
                        <input 
                            type="number" 
                            class="form-control" 
                            id="mesVencimiento" 
                            v-model.number="nuevaTarjeta.mesVencimiento" 
                            min="1" max="12" 
                            required
                            :disabled="isCreating"
                        />
                    </div>
                    <div class="col-6">
                        <label for="anioVencimiento" class="form-label">Año Exp.</label>
                        <input 
                            type="number" 
                            class="form-control" 
                            id="anioVencimiento" 
                            v-model.number="nuevaTarjeta.anioVencimiento" 
                            :min="new Date().getFullYear()" :max="new Date().getFullYear() + 10" 
                            required
                            :disabled="isCreating"
                        />
                    </div>
                </div>

                <button 
                    type="submit" 
                    class="btn btn-primary w-100" 
                    :disabled="isCreating"
                >
                    <span v-if="isCreating" class="spinner-border spinner-border-sm me-2"></span>
                    Guardar Tarjeta
                </button>
            </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { obtenerTarjetasUsuario, crearTarjeta, eliminarTarjeta } from '@/api/tarjetas'; 

const tarjetas = ref([]);
const isLoading = ref(false);
const isCreating = ref(false);
const isDeleting = ref(false);

const errorFormulario = ref('');
const successFormulario = ref('');

const nuevaTarjeta = ref({
    numeroTarjeta: '',
    titular: '',
    mesVencimiento: null,
    anioVencimiento: null,
});

// --- Lógica de Carga ---

const cargarTarjetas = async () => {
    isLoading.value = true;
    try {
        tarjetas.value = await obtenerTarjetasUsuario();
    } catch (error) {
        errorFormulario.value = "Error al cargar las tarjetas: " + (error.response?.data || error.message);
    } finally {
        isLoading.value = false;
    }
};

// --- Lógica de Creación ---

const handleCrearTarjeta = async () => {
    isCreating.value = true;
    errorFormulario.value = '';
    successFormulario.value = '';

    try {
        // Valida que el año no sea pasado, aunque el backend también lo hará
        if (nuevaTarjeta.value.anioVencimiento < new Date().getFullYear()) {
             errorFormulario.value = "El año de vencimiento no puede ser pasado.";
             return;
        }

        await crearTarjeta(nuevaTarjeta.value);
        
        successFormulario.value = 'Tarjeta guardada con éxito.';
        // Recargar la lista y limpiar el formulario
        await cargarTarjetas(); 
        resetForm();

    } catch (error) {
        // El backend devuelve el mensaje de error de validación (400) o DB (403, 409)
        const message = error.response?.data || 'Error al agregar tarjeta. Intenta de nuevo.';
        errorFormulario.value = typeof message === 'string' ? message : 'Error de validación.';
        console.error("Error al crear tarjeta:", error);
    } finally {
        isCreating.value = false;
    }
};

const resetForm = () => {
    nuevaTarjeta.value = {
        numeroTarjeta: '',
        titular: '',
        mesVencimiento: null,
        anioVencimiento: null,
    };
    // Opcional: limpiar el mensaje de éxito después de un tiempo
    setTimeout(() => { successFormulario.value = ''; }, 3000); 
};

// --- Lógica de Eliminación ---

const confirmarEliminacion = async (tarjeta) => {
    if (confirm(`¿Estás seguro de que quieres eliminar la tarjeta ****${tarjeta.parteVisible} de ${tarjeta.titular}?`)) {
        handleEliminarTarjeta(tarjeta.id);
    }
};

const handleEliminarTarjeta = async (tarjetaId) => {
    isDeleting.value = true;
    errorFormulario.value = '';
    
    try {
        await eliminarTarjeta(tarjetaId);
        
        // Recargar la lista sin el elemento eliminado
        await cargarTarjetas(); 
        successFormulario.value = 'Tarjeta eliminada con éxito.';
        setTimeout(() => { successFormulario.value = ''; }, 3000); 

    } catch (error) {
        const message = error.response?.data || 'Error al eliminar tarjeta. Intenta de nuevo.';
        errorFormulario.value = typeof message === 'string' ? message : 'Error de eliminación.';
    } finally {
        isDeleting.value = false;
    }
};

onMounted(cargarTarjetas);
</script>

<style scoped>
.form-card {
    top: 20px; /* Asegura que la tarjeta del formulario esté siempre visible */
}
</style>