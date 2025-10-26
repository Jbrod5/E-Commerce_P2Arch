<template>
  <div class="container-fluid bg-light min-vh-100 p-4">
    <div class="row">
      <div class="col-12">
        <h1 class="h2 fw-bold text-dark mb-4 border-bottom pb-3">
          <i class="fas fa-gavel text-danger me-2"></i> Gestion de Sanciones de Vendedores
        </h1>

        <!-- Mensaje de Alerta General -->
        <div v-if="mensajeGeneral" class="alert mb-4" :class="esExito ? 'alert-success' : 'alert-danger'">
          <p class="fw-bold mb-1">{{ esExito ? '¡Éxito!' : 'Error:' }}</p>
          <p class="mb-0">{{ mensajeGeneral }}</p>
        </div>

        <!-- Indicador de Carga de Usuarios -->
        <div v-if="cargandoUsuarios" class="text-center p-5 text-muted">
          <div class="spinner-border spinner-border-sm me-2" role="status"></div>
          Cargando lista de vendedores...
        </div>

        <!-- Tabla de Vendedores -->
        <div v-else class="card shadow-sm border-0">
          <div class="card-body p-0">
            <div v-if="vendedores.length > 0" class="table-responsive">
              <table class="table table-hover mb-0">
                <thead class="table-light">
                  <tr>
                    <th scope="col" class="ps-4">ID</th>
                    <th scope="col">Nombre</th>
                    <th scope="col">Correo</th>
                    <th scope="col" class="text-center">Estado</th>
                    <th scope="col" class="text-center">Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="vendedor in vendedores" :key="vendedor.id" :class="{'table-warning': !vendedor.activo}">
                    <td class="ps-4 fw-medium">{{ vendedor.id }}</td>
                    <td>{{ vendedor.nombre }}</td>
                    <td>{{ vendedor.correo }}</td>
                    <td class="text-center">
                      <span 
                        class="badge"
                        :class="vendedor.activo ? 'bg-success' : 'bg-danger'"
                      >
                        {{ vendedor.activo ? 'Activo' : 'Sancionado' }}
                      </span>
                    </td>
                    <td class="text-center">
                      <button 
                        @click="abrirModalSancion(vendedor)"
                        :disabled="!vendedor.activo"
                        class="btn btn-sm"
                        :class="vendedor.activo ? 'btn-outline-danger' : 'btn-outline-secondary'"
                        title="Sancionar usuario"
                      >
                        <i class="fas fa-ban me-1"></i> 
                        {{ vendedor.activo ? 'Sancionar' : 'Ya Sancionado' }}
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            <div v-else class="text-center p-5 text-muted">
              <i class="fas fa-users-slash fa-2x mb-3"></i>
              <p class="mb-0">No se encontraron usuarios vendedores (rol 'comun') en la base de datos.</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal de Sanción -->
    <div v-if="mostrarModal" class="modal fade show d-block" tabindex="-1" style="background-color: rgba(0,0,0,0.5)">
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header bg-light">
            <h5 class="modal-title text-danger">
              <i class="fas fa-gavel me-2"></i> Aplicar Sanción a Vendedor
            </h5>
            <button type="button" class="btn-close" @click="cerrarModal"></button>
          </div>

          <div class="modal-body">
            <p class="text-muted mb-3">
              Va a sancionar a: <strong>{{ usuarioSeleccionado.nombre }} (ID: {{ usuarioSeleccionado.id }})</strong>
            </p>

            <form @submit.prevent="confirmarSancion">
              <!-- Motivo de la Sanción -->
              <div class="mb-3">
                <label for="motivoModal" class="form-label">Motivo de la sanción</label>
                <textarea 
                  id="motivoModal" 
                  v-model="sancionData.motivo" 
                  required
                  rows="3"
                  placeholder="Describa claramente el motivo de la suspensión."
                  class="form-control"
                ></textarea>
              </div>

              <!-- Fecha de Fin -->
              <div class="mb-4">
                <label for="fechaFinModal" class="form-label">Fecha y Hora de Fin de la Sanción</label>
                <input 
                  type="datetime-local" 
                  id="fechaFinModal" 
                  v-model="sancionData.fechaFin" 
                  required
                  class="form-control"
                  :class="{'is-invalid': !esFechaValida && sancionData.fechaFin}"
                >
                <div v-if="!esFechaValida && sancionData.fechaFin" class="invalid-feedback">
                  La fecha debe ser futura.
                </div>
              </div>

              <!-- Botones de Acción -->
              <div class="d-flex justify-content-end gap-2">
                <button 
                  type="button" 
                  @click="cerrarModal"
                  class="btn btn-outline-secondary"
                  :disabled="cargandoPeticion"
                >
                  Cancelar
                </button>
                <button 
                  type="submit" 
                  :disabled="cargandoPeticion || !esFechaValida"
                  class="btn btn-danger"
                >
                  <span v-if="!cargandoPeticion">
                    <i class="fas fa-ban me-1"></i> Confirmar Sanción
                  </span>
                  <span v-else>
                    <div class="spinner-border spinner-border-sm me-2"></div>
                    Sancionando...
                  </span>
                </button>
              </div>
            </form>
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
const cargandoPeticion = ref(false);
const mensajeGeneral = ref('');
const esExito = ref(false);

// --- ESTADO DEL MODAL ---
const mostrarModal = ref(false);
const usuarioSeleccionado = ref(null);
const sancionData = ref({
  idUsuarioASuspender: null,
  motivo: '',
  fechaFin: '',
});

// --- COMPUTADAS ---
const esFechaValida = computed(() => {
  const fechaFin = new Date(sancionData.value.fechaFin);
  const ahora = new Date();
  return !isNaN(fechaFin) && fechaFin > ahora;
});

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

const abrirModalSancion = (usuario) => {
  if (!usuario.activo) return;

  usuarioSeleccionado.value = usuario;
  sancionData.value.idUsuarioASuspender = usuario.id;
  sancionData.value.motivo = '';
  
  const fechaDefault = new Date();
  fechaDefault.setDate(fechaDefault.getDate() + 7);
  sancionData.value.fechaFin = fechaDefault.toISOString().slice(0, 16); 
  
  mostrarModal.value = true;
  mensajeGeneral.value = '';
};

const cerrarModal = () => {
  mostrarModal.value = false;
  usuarioSeleccionado.value = null;
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
    const fechaFinLegible = new Date(response.data.fechaFin).toLocaleString('es-GT', { dateStyle: 'full', timeStyle: 'short' });
    mensajeGeneral.value = `¡Usuario ${usuarioSeleccionado.value.nombre} (ID: ${usuarioSeleccionado.value.id}) sancionado exitosamente hasta el ${fechaFinLegible} por: ${sancionData.value.motivo}.`;
    
    await cargarVendedores(); 
    cerrarModal();

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

// --- HOOKS DE VUE ---
onMounted(() => {
  cargarVendedores();
});
</script>

<style scoped>
.table-responsive {
  border-radius: 0.375rem;
}
.modal {
  backdrop-filter: blur(2px);
}
</style>