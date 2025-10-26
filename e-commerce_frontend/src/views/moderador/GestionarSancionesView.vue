<template>
  <!-- Contenedor principal con estilo Bootstrap (fondo claro, padding) -->
  <div class="bg-light min-vh-100 py-4 py-md-5">
    <div class="container">
      
      <!-- Encabezado (H1 con border-bottom) -->
      <h1 class="text-3xl font-weight-bold text-dark mb-4 pb-2 border-bottom border-secondary">
        <i class="fas fa-gavel text-danger me-2"></i> Gestión de Sanciones de Vendedores
      </h1>

      <!-- Mensaje de Alerta General (alert alert-success/danger) -->
      <div v-if="mensajeGeneral" 
           class="alert alert-dismissible fade show shadow-sm" 
           :class="{'alert-success': esExito, 'alert-danger': !esExito}">
        <p class="font-weight-bold d-flex align-items-center">
          <i :class="esExito ? 'fas fa-check-circle' : 'fas fa-exclamation-triangle'" class="me-2"></i>
          {{ esExito ? 'Operación Exitosa' : 'Alerta de Error' }}:
        </p>
        <p class="mb-0 small">{{ mensajeGeneral }}</p>
      </div>

      <!-- Indicador de Carga (Spinner centrado) -->
      <div v-if="cargandoUsuarios" class="text-center p-5 bg-white rounded shadow">
        <div class="spinner-border text-primary me-2" role="status">
          <span class="visually-hidden">Cargando...</span>
        </div>
        <span class="text-secondary fw-bold">Cargando lista de vendedores...</span>
      </div>

      <!-- Tabla de Vendedores (table table-striped y card-like) -->
      <div v-else class="card shadow-lg border-0">
        <div class="card-body p-0">
          <div v-if="vendedores.length > 0" class="table-responsive">
            <table class="table table-striped table-hover mb-0">
              <thead class="table-light">
                <tr>
                  <th scope="col" class="px-3 py-3 text-start small fw-bold text-secondary">ID</th>
                  <th scope="col" class="px-3 py-3 text-start small fw-bold text-secondary">Nombre</th>
                  <th scope="col" class="px-3 py-3 text-start small fw-bold text-secondary">Correo</th>
                  <th scope="col" class="px-3 py-3 text-center small fw-bold text-secondary">Estado</th>
                  <th scope="col" class="px-3 py-3 text-center small fw-bold text-secondary">Acciones</th>
                </tr>
              </thead>
              <tbody>
                <tr 
                  v-for="vendedor in vendedores" 
                  :key="vendedor.id" 
                  :class="{'table-danger': !vendedor.activo}"
                >
                  <td class="px-3 py-3 small fw-bold text-dark">{{ vendedor.id }}</td>
                  <td class="px-3 py-3 small text-secondary">{{ vendedor.nombre }}</td>
                  <td class="px-3 py-3 small text-secondary">{{ vendedor.correo }}</td>
                  <td class="px-3 py-3 text-center">
                    <!-- Badge (rounded-pill) -->
                    <span 
                      :class="{'bg-success': vendedor.activo, 'bg-danger': !vendedor.activo}"
                      class="badge rounded-pill shadow-sm"
                    >
                      {{ vendedor.activo ? 'Activo' : 'Suspendido' }}
                    </span>
                  </td>
                  <td class="px-3 py-3 text-center d-flex justify-content-center gap-2">
                    <!-- Botón para ver Historial -->
                    <button 
                      @click="abrirModalHistorial(vendedor)"
                      class="btn btn-info btn-sm shadow-sm text-white"
                      title="Ver Historial de Sanciones"
                    >
                      <i class="fas fa-history me-1"></i> Historial
                    </button>
                    <!-- Botón de Sanción (btn btn-danger) -->
                    <button 
                      @click="abrirModalSancion(vendedor)"
                      :disabled="!vendedor.activo"
                      class="btn btn-danger btn-sm shadow-sm"
                      :class="{'disabled': !vendedor.activo}"
                      title="Sancionar usuario"
                    >
                      <i class="fas fa-ban me-1"></i> {{ vendedor.activo ? 'Sancionar' : 'Ya Suspendido' }}
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <div v-else class="p-4 text-center text-secondary small">
            <i class="fas fa-info-circle me-2"></i> No se encontraron usuarios vendedores (rol 'comun').
          </div>
        </div>
      </div>
    </div>
    
    <!-- Modal de Sanción -->
    <div v-if="mostrarModalSancion" class="modal fade show d-block" tabindex="-1" style="background-color: rgba(0,0,0,0.5)">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content shadow-lg">
          
          <!-- modal-header -->
          <div class="modal-header bg-danger text-white">
            <h5 class="modal-title fw-bold">Aplicar Suspensión</h5>
            <button type="button" class="btn-close btn-close-white" @click="cerrarModalSancion" aria-label="Close"></button>
          </div>

          <!-- modal-body -->
          <div class="modal-body">
            <div class="alert alert-warning border-start border-5 border-danger p-3 mb-4">
              <i class="fas fa-user-shield me-2"></i>
              Sancionando a: <strong class="text-danger">{{ usuarioSeleccionado.nombre }} (ID: {{ usuarioSeleccionado.id }})</strong>
            </div>

            <form @submit.prevent="confirmarSancion">
              
              <!-- Form Group: Motivo -->
              <div class="mb-3">
                <label for="motivoModal" class="form-label small fw-bold text-secondary">Motivo de la Suspensión <span class="text-danger">*</span></label>
                <textarea 
                  id="motivoModal" 
                  v-model="sancionData.motivo" 
                  required
                  rows="3"
                  placeholder="Describa claramente el incumplimiento."
                  class="form-control"
                ></textarea>
              </div>

              <!-- Form Group: Fecha de Fin -->
              <div class="mb-3">
                <label for="fechaFinModal" class="form-label small fw-bold text-secondary">Fecha y Hora de Fin de la Sanción <span class="text-danger">*</span></label>
                <input 
                  type="datetime-local" 
                  id="fechaFinModal" 
                  v-model="sancionData.fechaFin" 
                  required
                  class="form-control"
                >
                <!-- Mensaje de validación -->
                <div v-if="!esFechaValida" class="form-text text-danger d-flex align-items-center">
                  <i class="fas fa-exclamation-circle me-1"></i> La fecha de fin debe ser válida y estrictamente futura.
                </div>
              </div>
            </form>
          </div>
          
          <!-- modal-footer -->
          <div class="modal-footer">
            <!-- btn btn-secondary -->
            <button 
              type="button" 
              @click="cerrarModalSancion"
              class="btn btn-secondary shadow-sm"
              :disabled="cargandoPeticion"
            >
              <i class="fas fa-times me-1"></i> Cerrar
            </button>
            <!-- btn btn-danger -->
            <button 
              type="submit" 
              @click="confirmarSancion"
              :disabled="cargandoPeticion || !esFechaValida || sancionData.motivo.length < 5"
              class="btn btn-danger shadow-sm"
              :class="{'disabled': cargandoPeticion || !esFechaValida || sancionData.motivo.length < 5}"
            >
              <span v-if="!cargandoPeticion"><i class="fas fa-gavel me-1"></i> Aplicar Sanción</span>
              <span v-else>
                <span class="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></span>
                Procesando...
              </span>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Nuevo Modal de Historial de Sanciones -->
    <div v-if="mostrarModalHistorial" class="modal fade show d-block" tabindex="-1" style="background-color: rgba(0,0,0,0.5)">
      <div class="modal-dialog modal-xl modal-dialog-centered modal-dialog-scrollable">
        <div class="modal-content shadow-lg">
          
          <div class="modal-header bg-info text-white">
            <h5 class="modal-title fw-bold">
              <i class="fas fa-history me-2"></i> Historial de Sanciones
            </h5>
            <button type="button" class="btn-close btn-close-white" @click="cerrarModalHistorial" aria-label="Close"></button>
          </div>

          <div class="modal-body">
            <p class="mb-3 text-muted">
              Historial completo para: <strong>{{ usuarioSeleccionado?.nombre }} (ID: {{ usuarioSeleccionado?.id }})</strong>
            </p>

            <div v-if="cargandoHistorial" class="text-center p-5">
              <div class="spinner-border text-info me-2" role="status">
                <span class="visually-hidden">Cargando historial...</span>
              </div>
              <span class="text-info fw-bold">Cargando historial de sanciones...</span>
            </div>

            <div v-else-if="historialSanciones.length > 0" class="list-group">
              <div 
                v-for="sancion in historialSanciones" 
                :key="sancion.id" 
                class="list-group-item list-group-item-action mb-2 shadow-sm border"
                :class="sancion.activa ? 'border-danger' : 'border-success'"
              >
                <div class="d-flex w-100 justify-content-between">
                  <h6 class="mb-1 fw-bold text-danger">
                    <i class="fas fa-gavel me-2"></i> Sanción #{{ sancion.id }}
                  </h6>
                  <small 
                    class="badge rounded-pill p-2"
                    :class="sancion.activa ? 'bg-danger' : 'bg-success'"
                  >
                    {{ sancion.activa ? 'ACTIVA' : 'FINALIZADA' }}
                  </small>
                </div>

                <div class="row small mt-2">
                  <div class="col-md-6">
                    <p class="mb-1 text-muted"><strong>Inicio:</strong> {{ formatoFecha(sancion.fechaSuspension) }}</p>
                    <p class="mb-1 text-muted"><strong>Fin:</strong> {{ formatoFecha(sancion.fechaFin) }}</p>
                  </div>
                  <div class="col-md-6">
                    <p class="mb-1 text-muted"><strong>Moderador:</strong> {{ sancion.nombreModerador || 'N/A' }}</p>
                  </div>
                </div>

                <p class="mb-1 mt-2 p-2 bg-light border-start border-4 border-secondary rounded">
                    <strong class="text-dark">Motivo:</strong> {{ sancion.motivo }}
                </p>

              </div>
            </div>

            <div v-else class="text-center p-5 text-muted">
              <i class="fas fa-check-circle fa-2x mb-3 text-success"></i>
              <p>Este vendedor no tiene sanciones registradas.</p>
            </div>
          </div>

          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="cerrarModalHistorial">
              <i class="fas fa-times me-1"></i> Cerrar
            </button>
          </div>
        </div>
      </div>
    </div>


  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import ModeradorAPI from '@/api/ModeradorApi';

// --- ESTADO GENERAL ---
const vendedores = ref([]);
const cargandoUsuarios = ref(false);
const cargandoPeticion = ref(false); // Para el spinner de confirmación
const mensajeGeneral = ref('');
const esExito = ref(false);

// --- ESTADO MODAL DE SANCIÓN ---
const mostrarModalSancion = ref(false);
const usuarioSeleccionado = ref(null);
const sancionData = ref({
  idUsuarioASuspender: null,
  motivo: '',
  fechaFin: '',
});

// --- ESTADO MODAL DE HISTORIAL ---
const mostrarModalHistorial = ref(false);
const cargandoHistorial = ref(false);
const historialSanciones = ref([]);


// --- COMPUTADAS ---
const esFechaValida = computed(() => {
  const fechaFin = new Date(sancionData.value.fechaFin);
  const ahora = new Date();
  return !isNaN(fechaFin) && fechaFin > ahora;
});

// --- FUNCIONES DE FORMATO ---
const formatoFecha = (fechaISO) => {
  if (!fechaISO) return 'N/A';
  // Formato localizado para mayor claridad (ej: "26 oct. 2025, 12:00")
  return new Date(fechaISO).toLocaleString('es-GT', { 
    dateStyle: 'medium', 
    timeStyle: 'short' 
  });
};

// --- FUNCIONES DE GESTIÓN DE DATOS ---
const cargarVendedores = async () => {
  cargandoUsuarios.value = true;
  try {
    const response = await ModeradorAPI.obtenerUsuariosVendedores();
    vendedores.value = response.data;
    mensajeGeneral.value = `Se cargaron ${vendedores.value.length} usuarios vendedores.`;
    esExito.value = true;
  } catch (error) {
    console.error("Error al cargar vendedores:", error);
    mensajeGeneral.value = 'Error al cargar la lista de vendedores. Verifique su conexión y permisos.';
    esExito.value = false;
  } finally {
    cargandoUsuarios.value = false;
  }
};


// --- CONTROL DEL MODAL DE SANCIÓN ---

const abrirModalSancion = (usuario) => {
  if (!usuario.activo) return;

  usuarioSeleccionado.value = usuario;
  sancionData.value.idUsuarioASuspender = usuario.id;
  sancionData.value.motivo = '';
  
  // Fecha de fin por defecto: 7 días después
  const fechaDefault = new Date();
  fechaDefault.setDate(fechaDefault.getDate() + 7);
  sancionData.value.fechaFin = fechaDefault.toISOString().slice(0, 16); 
  
  mostrarModalSancion.value = true;
  mensajeGeneral.value = '';
};

const cerrarModalSancion = () => {
  mostrarModalSancion.value = false;
  // Solo reseteamos la data de sanción
  sancionData.value.idUsuarioASuspender = null;
  sancionData.value.motivo = '';
  sancionData.value.fechaFin = '';
};

const confirmarSancion = async () => {
  if (!esFechaValida.value) {
    mensajeGeneral.value = 'Por favor, ingrese una fecha de fin válida y futura.';
    esExito.value = false;
    return;
  }
  
  cargandoPeticion.value = true;
  
  try {
    const dataParaBackend = {
      idUsuarioASuspender: sancionData.value.idUsuarioASuspender,
      motivo: sancionData.value.motivo,
      fechaFin: sancionData.value.fechaFin, 
    };

    const response = await ModeradorAPI.sancionarUsuario(dataParaBackend);

    esExito.value = true;
    const fechaFinLegible = formatoFecha(response.data.fechaFin);
    mensajeGeneral.value = `¡Usuario ${usuarioSeleccionado.value.nombre} (ID: ${usuarioSeleccionado.value.id}) sancionado exitosamente hasta el ${fechaFinLegible} por: ${sancionData.value.motivo}.`;
    
    await cargarVendedores(); 
    cerrarModalSancion();

  } catch (error) {
    esExito.value = false;
    let errorMsg = 'Error desconocido al aplicar la sanción.';
    
    if (error.response?.data?.error) {
        errorMsg = error.response.data.error;
    } else if (error.message) {
        errorMsg = `Error de red: ${error.message}`;
    }
    
    mensajeGeneral.value = errorMsg;
    
  } finally {
    cargandoPeticion.value = false;
  }
};


// --- CONTROL DEL MODAL DE HISTORIAL ---

const abrirModalHistorial = async (usuario) => {
  usuarioSeleccionado.value = usuario;
  historialSanciones.value = [];
  mostrarModalHistorial.value = true;
  cargandoHistorial.value = true;

  try {
    const response = await ModeradorAPI.obtenerHistorialSanciones(usuario.id);
    historialSanciones.value = response.data;
  } catch (error) {
    console.error("Error al cargar historial:", error);
    // En caso de error, simplemente mostramos el mensaje de "No hay sanciones" o un mensaje de error dentro del modal
  } finally {
    cargandoHistorial.value = false;
  }
};

const cerrarModalHistorial = () => {
  mostrarModalHistorial.value = false;
  historialSanciones.value = [];
};


// --- HOOKS DE VUE ---
onMounted(() => {
  cargarVendedores();
});
</script>

<style scoped>
.table-responsive {
  border-radius: 0.375rem;
}
/* Estilo necesario para simular el comportamiento de Bootstrap Show/Hide con v-if */
.modal.show.d-block {
    display: block;
}
.modal {
  backdrop-filter: blur(2px);
  overflow-y: auto;
}
</style>
