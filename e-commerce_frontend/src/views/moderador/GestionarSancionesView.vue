<template>
  <div class="container py-4">
    <!-- Encabezado -->
    <div class="d-flex align-items-center mb-4">
      <i class="fas fa-gavel text-danger fs-4 me-3"></i>
      <h1 class="h3 mb-0 text-dark">Gestión de Sanciones</h1>
    </div>

    <!-- Alerta General -->
    <div
      v-if="mensajeGeneral"
      class="alert mb-4"
      :class="esExito ? 'alert-success' : 'alert-danger'"
    >
      {{ mensajeGeneral }}
    </div>

    <!-- Cargando -->
    <div v-if="cargandoUsuarios" class="text-center py-5">
      <div class="spinner-border text-primary"></div>
      <p class="text-muted mt-2">Cargando vendedores...</p>
    </div>

    <!-- Tabla de Vendedores -->
    <div v-else class="card">
      <div class="card-body">
        <div v-if="vendedores.length > 0" class="table-responsive">
          <table class="table table-hover">
            <thead>
              <tr>
                <th>ID</th>
                <th>Nombre</th>
                <th>Correo</th>
                <th class="text-center">Estado</th>
                <th class="text-center">Acciones</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="vendedor in vendedores" :key="vendedor.id">
                <td class="fw-semibold">{{ vendedor.id }}</td>
                <td>{{ vendedor.nombre }}</td>
                <td>{{ vendedor.correo }}</td>
                <td class="text-center">
                  <span
                    class="badge"
                    :class="vendedor.activo ? 'bg-success' : 'bg-danger'"
                  >
                    {{ vendedor.activo ? "Activo" : "Sancionado" }}
                  </span>
                </td>
                <td class="text-center">
                  <div class="d-flex justify-content-center gap-2">
                    <button
                      @click="abrirModalHistorial(vendedor)"
                      class="btn btn-outline-primary btn-sm"
                    >
                      <i class="fas fa-history me-1"></i>Historial
                    </button>




                    <button
                      v-if="vendedor.activo"
                      @click="abrirModalSancion(vendedor)"
                      :disabled="!vendedor.activo"
                      class="btn btn-sm"
                      :class="
                        vendedor.activo ? 'btn-outline-danger' : 'btn-outline-secondary'
                      "
                    >
                      <i class="fas fa-ban me-1"></i>
                      {{ vendedor.activo ? "Sancionar" : "Sancionado" }}
                    </button>




                    <button
                      v-if="!vendedor.activo"
                      @click="confirmarLevantarSancion(vendedor)"
                      :disabled="cargandoPeticion"
                      class="btn btn-outline-success btn-sm"
                    >
                      <i class="fas fa-undo-alt me-1"></i>
                      Levantar Sanción
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div v-else class="text-center py-4 text-muted">
          <i class="fas fa-users-slash fa-2x mb-3"></i>
          <p>No se encontraron vendedores</p>
        </div>
      </div>
    </div>

    <!-- Modal Sanción -->
    <div
      v-if="mostrarModalSancion"
      class="modal show d-block"
      style="background-color: rgba(0, 0, 0, 0.5)"
    >
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Aplicar Sanción</h5>
            <button type="button" class="btn-close" @click="cerrarModalSancion"></button>
          </div>
          <div class="modal-body">
            <p class="mb-3">
              Vendedor: <strong>{{ usuarioSeleccionado.nombre }}</strong> (ID:
              {{ usuarioSeleccionado.id }})
            </p>

            <div class="mb-3">
              <label class="form-label">Motivo</label>
              <textarea
                v-model="sancionData.motivo"
                class="form-control"
                rows="3"
                placeholder="Describe el motivo..."
              ></textarea>
            </div>

            <div class="mb-3">
              <label class="form-label">Fecha de Fin</label>
              <input
                type="datetime-local"
                v-model="sancionData.fechaFin"
                class="form-control"
              />
              <div v-if="!esFechaValida" class="form-text text-danger">
                La fecha debe ser futura
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="cerrarModalSancion">
              Cancelar
            </button>
            <button
              type="button"
              class="btn btn-danger"
              @click="confirmarSancion"
              :disabled="cargandoPeticion || !esFechaValida || !sancionData.motivo"
            >
              <span
                v-if="cargandoPeticion"
                class="spinner-border spinner-border-sm me-2"
              ></span>
              Confirmar Sanción
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Historial -->
    <div
      v-if="mostrarModalHistorial"
      class="modal show d-block"
      style="background-color: rgba(0, 0, 0, 0.5)"
    >
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Historial de Sanciones</h5>
            <button
              type="button"
              class="btn-close"
              @click="cerrarModalHistorial"
            ></button>
          </div>
          <div class="modal-body">
            <p class="text-muted mb-3">
              Vendedor: <strong>{{ usuarioSeleccionado?.nombre }}</strong>
            </p>

            <div v-if="cargandoHistorial" class="text-center py-4">
              <div class="spinner-border text-primary"></div>
            </div>

            <div v-else-if="historialSanciones.length > 0">
              <div
                v-for="sancion in historialSanciones"
                :key="sancion.id"
                class="card mb-2"
              >
                <div class="card-body">
                  <div class="d-flex justify-content-between align-items-start">
                    <div>
                      <span
                        class="badge me-2"
                        :class="sancion.activa ? 'bg-danger' : 'bg-success'"
                      >
                        {{ sancion.activa ? "Activa" : "Finalizada" }}
                      </span>
                      <small class="text-muted">#{{ sancion.id }}</small>
                    </div>
                    <small class="text-muted">{{
                      formatoFecha(sancion.fechaSuspension)
                    }}</small>
                  </div>
                  <p class="mb-1 mt-2"><strong>Motivo:</strong> {{ sancion.motivo }}</p>
                  <p class="mb-0 text-muted small">
                    <strong>Fin:</strong> {{ formatoFecha(sancion.fechaFin) }}
                  </p>
                </div>
              </div>
            </div>

            <div v-else class="text-center py-4 text-muted">
              <i class="fas fa-check-circle fa-2x mb-3"></i>
              <p>No hay sanciones registradas</p>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="cerrarModalHistorial">
              Cerrar
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import ModeradorAPI from "@/api/moderador";

// Estados
const vendedores = ref([]);
const cargandoUsuarios = ref(false);
const cargandoPeticion = ref(false);
const mensajeGeneral = ref("");
const esExito = ref(false);

// Modal Sanción
const mostrarModalSancion = ref(false);
const usuarioSeleccionado = ref(null);
const sancionData = ref({
  idUsuarioASuspender: null,
  motivo: "",
  fechaFin: "",
});

// Modal Historial
const mostrarModalHistorial = ref(false);
const cargandoHistorial = ref(false);
const historialSanciones = ref([]);

// Computadas
const esFechaValida = computed(() => {
  const fechaFin = new Date(sancionData.value.fechaFin);
  const ahora = new Date();
  return !isNaN(fechaFin) && fechaFin > ahora;
});

// Funciones
const formatoFecha = (fechaISO) => {
  if (!fechaISO) return "N/A";
  return new Date(fechaISO).toLocaleString("es-GT", {
    dateStyle: "short",
    timeStyle: "short",
  });
};

const cargarVendedores = async () => {
  cargandoUsuarios.value = true;
  try {
    const response = await ModeradorAPI.obtenerUsuariosVendedores();
    vendedores.value = response.data;
  } catch (error) {
    mensajeGeneral.value = "Error al cargar vendedores";
    esExito.value = false;
  } finally {
    cargandoUsuarios.value = false;
  }
};

const abrirModalSancion = (usuario) => {
  if (!usuario.activo) return;

  usuarioSeleccionado.value = usuario;
  sancionData.value.idUsuarioASuspender = usuario.id;
  sancionData.value.motivo = "";

  const fechaDefault = new Date();
  fechaDefault.setDate(fechaDefault.getDate() + 7);
  sancionData.value.fechaFin = fechaDefault.toISOString().slice(0, 16);

  mostrarModalSancion.value = true;
  mensajeGeneral.value = "";
};

const cerrarModalSancion = () => {
  mostrarModalSancion.value = false;
  sancionData.value.motivo = "";
  sancionData.value.fechaFin = "";
};

const confirmarSancion = async () => {
  if (!esFechaValida.value) return;

  cargandoPeticion.value = true;

  try {
    await ModeradorAPI.sancionarUsuario(sancionData.value);
    mensajeGeneral.value = `Usuario ${usuarioSeleccionado.value.nombre} sancionado`;
    esExito.value = true;
    await cargarVendedores();
    cerrarModalSancion();
  } catch (error) {
    mensajeGeneral.value = "Error al aplicar sanción";
    esExito.value = false;
  } finally {
    cargandoPeticion.value = false;
  }
};

/**
 * Llama a la API para levantar la sanción de un usuario.
 * @param {object} usuario El objeto del usuario a reactivar.
 */
const confirmarLevantarSancion = async (usuario) => {
    // Alerta de confirmación 
    if (!confirm(`¿Está seguro de levantar la sanción del usuario ${usuario.nombre}? Esto reactivará su cuenta.`)) {
        return;
    }

    cargandoPeticion.value = true;
    
    try {
        await ModeradorAPI.levantarSancion(usuario.id);
        
        mensajeGeneral.value = `¡Éxito! La sanción de ${usuario.nombre} ha sido levantada.`;
        esExito.value = true;

        // Recargar la lista para actualizar el estado 'Activo' en la tabla
        await cargarVendedores(); 
        
    } catch (error) {
        mensajeGeneral.value = error.response?.data?.error || 'Error al levantar la sanción. ' + error;
        
        esExito.value = false;
    } finally {
        cargandoPeticion.value = false;
    }
};

const abrirModalHistorial = async (usuario) => {
  usuarioSeleccionado.value = usuario;
  mostrarModalHistorial.value = true;
  cargandoHistorial.value = true;

  try {
    const response = await ModeradorAPI.obtenerHistorialSanciones(usuario.id);
    historialSanciones.value = response.data;
  } catch (error) {
    historialSanciones.value = [];
  } finally {
    cargandoHistorial.value = false;
  }
};

const cerrarModalHistorial = () => {
  mostrarModalHistorial.value = false;
};

onMounted(() => {
  cargarVendedores();
});
</script>

<style scoped>
.modal {
  backdrop-filter: blur(2px);
}
.table th {
  border-top: none;
  font-weight: 600;
}
</style>
