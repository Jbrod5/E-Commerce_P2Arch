<script setup>
import { ref, onMounted } from 'vue';
import api from '@/plugins/axios.js';

const notificaciones = ref([]);
const detalleSeleccionado = ref(null);
const noLeidasCount = ref(0);
const isLoading = ref(false);
const error = ref(null);

const callApi = async (asyncFunction) => {
    isLoading.value = true;
    error.value = null;
    try {
        const result = await asyncFunction();
        isLoading.value = false;
        return result;
    } catch (err) {
        console.error("API Call Error:", err);
        error.value = err.response?.data?.message || err.message || 'Ocurrió un error en la comunicación con el servidor.';
        isLoading.value = false;
        return null;
    }
};

const cargarNotificaciones = async () => {
    const fetchNotificaciones = async () => {
        const response = await api.get('/notificaciones');
        return response.data;
    };
    
    const data = await callApi(fetchNotificaciones);
    if (data) {
        notificaciones.value = data;
        noLeidasCount.value = data.filter(n => !n.leida).length;
    }
};

const cargarDetalleYMarcarLeida = async (idNotificacion) => {
    isLoading.value = true;
    
    const fetchDetalle = async () => {
        const response = await api.get(`/notificaciones/${idNotificacion}`);
        return response.data;
    };
    
    const data = await callApi(fetchDetalle);
    isLoading.value = false;
    
    if (data) {
        const notifIndex = notificaciones.value.findIndex(n => n.id === idNotificacion);
        
        if (notifIndex !== -1 && !notificaciones.value[notifIndex].leida) {
            notificaciones.value[notifIndex].leida = true;
            noLeidasCount.value = Math.max(0, noLeidasCount.value - 1);
        }
        
        detalleSeleccionado.value = data;
    }
};

const formatTime = (dateString) => {
    if (!dateString) return 'Fecha desconocida';
    try {
        const date = new Date(dateString);
        return date.toLocaleDateString('es-ES', { 
            day: 'numeric', 
            month: 'short',
            hour: '2-digit',
            minute: '2-digit'
        });
    } catch (e) {
        return dateString;
    }
};

onMounted(() => {
    cargarNotificaciones();
});
</script>

<template>
    <div class="container py-4">
        
        <!-- Título de la página -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div class="d-flex align-items-center gap-2">
                <i class="bi bi-bell-fill fs-3 text-primary"></i>
                <h1 class="h3 mb-0 fw-bold">Notificaciones</h1>
            </div>
            <span v-if="noLeidasCount > 0" class="badge bg-danger rounded-pill px-3 py-2">
                {{ noLeidasCount }} nueva{{ noLeidasCount > 1 ? 's' : '' }}
            </span>
        </div>

        <!-- Card principal -->
        <div class="card shadow-sm border-0">
            <div class="card-body p-0">
                <div class="row g-0">
                    
                    <!-- Lista de notificaciones -->
                    <div 
                        :class="[
                            'col-12',
                            detalleSeleccionado ? 'col-lg-5' : 'col-lg-12',
                            'border-end'
                        ]"
                        style="max-height: 600px; overflow-y: auto;"
                    >
                        
                        <!-- Loading -->
                        <div v-if="isLoading && !notificaciones.length" class="text-center py-5">
                            <div class="spinner-border text-primary mb-3" role="status">
                                <span class="visually-hidden">Cargando...</span>
                            </div>
                            <p class="text-muted mb-0">Cargando notificaciones...</p>
                        </div>
                        
                        <!-- Error -->
                        <div v-else-if="error" class="m-3">
                            <div class="alert alert-danger d-flex align-items-center" role="alert">
                                <i class="bi bi-exclamation-triangle-fill fs-5 me-2"></i>
                                <div class="flex-grow-1">
                                    <strong>Error:</strong> {{ error }}
                                </div>
                            </div>
                            <button @click="cargarNotificaciones" class="btn btn-primary w-100">
                                <i class="bi bi-arrow-clockwise me-2"></i>Reintentar
                            </button>
                        </div>

                        <!-- Empty -->
                        <div v-else-if="notificaciones.length === 0" class="text-center py-5">
                            <i class="bi bi-inbox fs-1 text-muted opacity-50 mb-3 d-block"></i>
                            <p class="text-muted mb-0">No tienes notificaciones</p>
                        </div>

                        <!-- Lista -->
                        <div v-else class="list-group list-group-flush">
                            <a 
                                v-for="n in notificaciones" 
                                :key="n.id"
                                href="#"
                                @click.prevent="cargarDetalleYMarcarLeida(n.id)"
                                :class="[
                                    'list-group-item list-group-item-action border-0 py-3',
                                    { 'bg-primary bg-opacity-10 border-start border-primary border-3': !n.leida },
                                    { 'active': detalleSeleccionado?.id === n.id }
                                ]"
                            >
                                <div class="d-flex w-100 justify-content-between align-items-start">
                                    <div class="flex-grow-1 me-2">
                                        <div class="d-flex align-items-center gap-2 mb-1">
                                            <span v-if="!n.leida" class="badge bg-primary rounded-circle p-1" style="width: 8px; height: 8px;"></span>
                                            <h6 :class="['mb-0', !n.leida ? 'fw-bold' : 'fw-normal']">
                                                {{ n.titulo }}
                                            </h6>
                                        </div>
                                    </div>
                                    <small class="text-muted text-nowrap">
                                        <i class="bi bi-clock me-1"></i>{{ formatTime(n.fecha) }}
                                    </small>
                                </div>
                            </a>
                        </div>

                    </div>

                    <!-- Detalle -->
                    <div 
                        :class="[
                            'col-12 col-lg-7',
                            { 'd-none d-lg-block': !detalleSeleccionado }
                        ]"
                        style="max-height: 600px; overflow-y: auto;"
                    >
                        
                        <!-- Detalle activo -->
                        <div v-if="detalleSeleccionado" class="p-4">
                            
                            <button 
                                @click="detalleSeleccionado = null" 
                                class="btn-close float-end d-lg-none"
                                aria-label="Cerrar"
                            ></button>

                            <div class="mb-4">
                                <h4 class="fw-bold mb-2">{{ detalleSeleccionado.titulo }}</h4>
                                <p class="text-muted small mb-0">
                                    <i class="bi bi-clock me-1"></i>
                                    {{ formatTime(detalleSeleccionado.fecha) }}
                                </p>
                            </div>

                            <div class="bg-light border-start border-primary border-4 rounded p-3 mb-3">
                                <p class="mb-0 lh-lg" style="white-space: pre-wrap;">{{ detalleSeleccionado.cuerpo }}</p>
                            </div>

                            <div v-if="detalleSeleccionado.leida" class="d-flex align-items-center gap-2 text-success small">
                                <i class="bi bi-check-circle-fill"></i>
                                <span class="fw-semibold">Leída</span>
                            </div>
                        </div>

                        <!-- Placeholder -->
                        <div v-else class="d-flex flex-column align-items-center justify-content-center h-100 text-muted py-5">
                            <i class="bi bi-chat-left-text opacity-25 mb-3" style="font-size: 4rem;"></i>
                            <p class="mb-0">Selecciona una notificación</p>
                        </div>

                    </div>

                </div>
            </div>
        </div>

    </div>
</template>

<style scoped>
/* Solo para el overlay móvil */
@media (max-width: 991.98px) {
    .col-lg-7 {
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        z-index: 1050;
        background: white;
        max-height: 100vh !important;
    }
}
</style>