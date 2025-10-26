<template>
  <div class="container-fluid">
    <h1 class="mb-4 text-primary fw-bold">
      <i class="bi bi-file-earmark-check me-2"></i> Solicitudes de Venta Pendientes
    </h1>
    <p class="text-secondary">Revisa y toma una decisión sobre los productos que los vendedores comunes quieren publicar.</p>

    <!-- Componente de Alerta/Mensaje -->
    <div v-if="mensajeExito" class="alert alert-success alert-dismissible fade show" role="alert">
        {{ mensajeExito }}
        <button type="button" class="btn-close" @click="mensajeExito = ''"></button>
    </div>
    <div v-if="mensajeError" class="alert alert-danger alert-dismissible fade show" role="alert">
        {{ mensajeError }}
        <button type="button" class="btn-close" @click="mensajeError = ''"></button>
    </div>

    <!-- Indicador de Carga -->
    <div v-if="cargando" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Cargando...</span>
      </div>
      <p class="mt-2">Cargando solicitudes...</p>
    </div>

    <!-- Lista de Solicitudes -->
    <div v-else-if="solicitudes.length > 0" class="row">
      <div v-for="solicitud in solicitudes" :key="solicitud.idProducto" class="col-lg-6 mb-4">
        <div class="card shadow-sm h-100">
          <div class="row g-0">
            <!-- Columna de Imagen -->
            <div class="col-md-4 d-flex align-items-center justify-content-center bg-light p-2 rounded-start">
                <!-- Usando la directiva v-ngrok-img para cargar la imagen -->
                <img 
                    v-ngrok-img="solicitud.imagenUrl" 
                    class="img-fluid rounded-3 object-fit-cover" 
                    alt="Imagen del producto" 
                    style="max-height: 150px; width: 100%;"
                    onerror="this.onerror=null;this.src='https://placehold.co/150x150/ADB5BD/ffffff?text=Sin+Imagen'"
                >
            </div>
            
            <!-- Columna de Detalles y Acciones -->
            <div class="col-md-8">
              <div class="card-body d-flex flex-column h-100">
                <h5 class="card-title text-truncate fw-bold text-dark mb-1" :title="solicitud.nombreProducto">{{ solicitud.nombreProducto }}</h5>
                <p class="card-text small text-muted mb-2">
                    Vendedor: <span class="fw-semibold text-primary">{{ solicitud.nombreVendedor }}</span>
                </p>
                <p class="card-text small text-muted mb-2">
                    Categoría: <span class="badge bg-secondary">{{ solicitud.nombreCategoria }}</span> | 
                    Estado: <span class="badge" :class="{'bg-success': solicitud.esNuevo, 'bg-warning text-dark': !solicitud.esNuevo}">
                        {{ solicitud.esNuevo ? 'Nuevo' : 'Usado' }}
                    </span>
                </p>
                <p class="card-text fs-5 fw-bold text-success mb-2">
                    Q {{ solicitud.precio ? solicitud.precio.toFixed(2) : '0.00' }}
                </p>
                <p class="card-text text-truncate mb-3">
                    Solicitud ID: {{ solicitud.idSolicitud }}
                </p>

                <!-- Acciones -->
                <div class="mt-auto d-flex justify-content-end gap-2 pt-2 border-top">
                  <button 
                    @click="iniciarRechazo(solicitud)" 
                    class="btn btn-outline-danger btn-sm"
                    :disabled="solicitud.procesando"
                  >
                    <span v-if="solicitud.procesando" class="spinner-border spinner-border-sm me-1"></span>
                    <i v-else class="bi bi-x-circle me-1"></i>Rechazar
                  </button>
                  <button 
                    @click="aprobarSolicitud(solicitud)" 
                    class="btn btn-success btn-sm"
                    :disabled="solicitud.procesando"
                  >
                    <span v-if="solicitud.procesando" class="spinner-border spinner-border-sm me-1"></span>
                    <i v-else class="bi bi-check-circle me-1"></i>Aprobar
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Sin Resultados -->
    <div v-else class="alert alert-info py-5 text-center" role="alert">
      <i class="bi bi-lightbulb-fill fs-3 mb-2"></i>
      <h4 class="alert-heading">¡Todo en Orden!</h4>
      <p class="mb-0">No hay solicitudes de productos pendientes de revisión.</p>
    </div>

    <!-- Modal de Rechazo -->
    <div class="modal fade" id="modalRechazo" tabindex="-1" aria-labelledby="modalRechazoLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header bg-danger text-white">
            <h5 class="modal-title" id="modalRechazoLabel">Rechazar Producto: {{ productoSeleccionado.nombreProducto }}</h5>
            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <p>Por favor, ingrese el motivo del rechazo. Este comentario será enviado al vendedor.</p>
            <div class="mb-3">
              <label for="motivoRechazo" class="form-label">Motivo (mínimo 10 caracteres, máximo 500):</label>
              <textarea 
                v-model="motivoRechazo" 
                class="form-control" 
                id="motivoRechazo" 
                rows="3"
                maxlength="500"
                :class="{'is-invalid': motivoRechazoError}"
              ></textarea>
              <div class="invalid-feedback">
                {{ motivoRechazoError }}
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
            <button 
              type="button" 
              class="btn btn-danger" 
              @click="rechazarSolicitud"
              :disabled="motivoRechazo.length < 10"
            >
              Confirmar Rechazo
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import ModeradorAPI from '@/api/ModeradorApi';
import { useModeradorStore } from "@/stores/moderador"; 
import { useAuthStore } from "@/stores/auth";

// Directivas Vue y Stores
const authStore = useAuthStore();
const moderadorStore = useModeradorStore(); 

// Estado de la vista
const solicitudes = ref([]);
const cargando = ref(true);
const mensajeExito = ref('');
const mensajeError = ref('');

// Estado para el modal de rechazo
const productoSeleccionado = ref({});
const motivoRechazo = ref('');
const motivoRechazoError = ref('');
let modalRechazoInstance = null; // Para guardar la instancia del modal

// Función de carga
const cargarSolicitudes = async () => {
    cargando.value = true;
    try {
        const response = await ModeradorAPI.obtenerSolicitudesPendientes();
        
        // Mapear la data y añadir el estado local 'procesando'
        solicitudes.value = response.data.map(p => ({
            ...p,
            procesando: false, 
            precio: p.precio ? parseFloat(p.precio) : 0.00 // Asegurar que el precio es un número
        }));
        
        // Actualizar el conteo en la Store para el Sidebar
        moderadorStore.setCantidadPendientes(solicitudes.value.length);
    } catch (error) {
        console.error('Error al cargar solicitudes:', error);
        mensajeError.value = error.response?.data?.error || 'Error al cargar las solicitudes de productos.';
    } finally {
        cargando.value = false;
    }
};

/**
 * Lógica unificada para procesar la decisión (aprobación o rechazo).
 * @param {object} solicitud - El objeto solicitud a procesar.
 * @param {boolean} aprobado - True para aprobar, False para rechazar.
 * @param {string} comentario - Comentario opcional/obligatorio para el rechazo.
 */
const procesarDecision = async (solicitud, aprobado, comentario = null) => {
    solicitud.procesando = true;
    mensajeExito.value = '';
    mensajeError.value = '';

    try {
        await ModeradorAPI.revisarProducto(solicitud.idProducto, aprobado, comentario);
        
        const accion = aprobado ? "aprobado" : "rechazado";
        mensajeExito.value = `¡Producto "${solicitud.nombreProducto}" ${accion} exitosamente! Se ha notificado al vendedor.`;
        
        // Eliminar el elemento de la lista y actualizar el conteo
        solicitudes.value = solicitudes.value.filter(p => p.idProducto !== solicitud.idProducto);
        moderadorStore.decrementarPendientes();

    } catch (error) {
        console.error(`Error al ${aprobado ? 'aprobar' : 'rechazar'} producto:`, error);
        mensajeError.value = error.response?.data?.message || `Error al procesar el producto: ${error.message}`;
        solicitud.procesando = false; // Habilitar botones de nuevo
    }
};

// Acciones específicas del UI
const aprobarSolicitud = (solicitud) => {
    // Aprobación sin comentario
    procesarDecision(solicitud, true, "Aprobado por el moderador.");
};

const iniciarRechazo = (solicitud) => {
    productoSeleccionado.value = solicitud;
    motivoRechazo.value = '';
    motivoRechazoError.value = '';
    // Mostrar el modal
    modalRechazoInstance.show();
};

const rechazarSolicitud = async () => {
    if (motivoRechazo.value.length < 10) {
        motivoRechazoError.value = 'El motivo debe tener al menos 10 caracteres.';
        return;
    }
    motivoRechazoError.value = '';

    // Ocultar modal antes de iniciar la solicitud API
    modalRechazoInstance.hide();
    
    // Llamar a la lógica unificada con rechazo (false) y el comentario
    await procesarDecision(productoSeleccionado.value, false, motivoRechazo.value);

    // Limpiar estado del modal
    productoSeleccionado.value = {};
    motivoRechazo.value = '';
};


onMounted(() => {
    // 1. Inicializar la instancia del modal de Bootstrap
    if (typeof bootstrap !== 'undefined') {
        modalRechazoInstance = new bootstrap.Modal(document.getElementById('modalRechazo'), {});
    }
    
    // 2. Cargar la data
        cargarSolicitudes();
    
});
</script>
