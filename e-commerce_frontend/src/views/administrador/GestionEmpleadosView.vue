<template>
  <div class="container-fluid p-0">
    <h1 class="mb-4 text-primary fw-bold border-bottom pb-2">Gestión de Empleados</h1>
    <p class="text-secondary mb-4">Administra el personal con roles de Moderador, Logística y Administrador.</p>
    
    <!-- Botón para abrir el modal de Registro -->
    <button 
      @click="abrirModalRegistro" 
      class="btn btn-primary mb-4 shadow-sm"
    >
      <i class="bi bi-person-plus-fill me-2"></i> Registrar Nuevo Empleado
    </button>

    <!-- Tabla para Visualizar Historial de Empleados -->
    <div class="card shadow-sm">
      <div class="card-header bg-light">
        <h5 class="mb-0 text-dark">Listado de Empleados</h5>
      </div>
      <div class="card-body p-0">
        <div class="table-responsive">
          <table class="table table-striped table-hover mb-0">
            <thead class="table-light">
              <tr>
                <th scope="col">ID</th>
                <th scope="col">Nombre</th>
                <th scope="col">Correo</th>
                <th scope="col">Rol</th>
                <th scope="col">Activo</th>
                <th scope="col">Acciones</th>
              </tr>
            </thead>
            <tbody>
              <!-- Indicador de carga -->
              <tr v-if="isLoading">
                <td colspan="6" class="text-center py-4 text-muted">Cargando empleados...</td>
              </tr>
              <!-- Lista de empleados -->
              <tr v-else v-for="empleado in empleados" :key="empleado.id || empleado.idUsuario">
                <!-- Se muestra la ID, usando 'empleado.id' o 'empleado.idUsuario' como fallback -->
                <td>{{ empleado.id || empleado.idUsuario }}</td>
                <td>{{ empleado.nombre }}</td>
                <td>{{ empleado.correo }}</td>
                <td><span :class="getBadgeClass(empleado.rol.nombre)">{{ empleado.rol.nombre }}</span></td>
                <td>
                  <span :class="empleado.activo ? 'text-success' : 'text-danger'">
                    <i :class="empleado.activo ? 'bi bi-check-circle-fill' : 'bi bi-x-octagon-fill'"></i>
                    {{ empleado.activo ? 'Sí' : 'No' }}
                  </span>
                </td>
                <td>
                  <button 
                    @click="abrirModalActualizacion(empleado)" 
                    class="btn btn-sm btn-outline-secondary me-2"
                  >
                    <i class="bi bi-pencil-square"></i> Editar
                  </button>
                </td>
              </tr>
              <tr v-if="!isLoading && empleados.length === 0">
                 <td colspan="6" class="text-center py-4 text-muted">No hay empleados registrados.</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
    
    <!-- COMPONENTE REAL DE REGISTRO -->
    <!-- Se usa el nombre correcto del componente: ModalRegistroEmpleado -->
    <ModalRegistroEmpleado 
        v-if="modalRegistroAbierto" 
        @cerrar="cerrarModalRegistro" 
        @empleado-actualizado="handleEmpleadoAccion" 
    />
    
    <!-- COMPONENTE REAL DE ACTUALIZACIÓN -->
    <ModalActualizacionEmpleado 
        v-if="modalActualizacionAbierto" 
        :empleado="empleadoAEditar" 
        @cerrar="cerrarModalActualizacion" 
        @empleado-actualizado="handleEmpleadoAccion" 
    />

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import adminApi from '@/api/admin.js';
// Importar componentes modales
import ModalActualizacionEmpleado from '@/components/administrador/ModalActualizacionEmpleado.vue'; 
import ModalRegistroEmpleado from '@/components/administrador/ModalRegistrarEmpleado.vue';

const empleados = ref([]);
const modalRegistroAbierto = ref(false);
const modalActualizacionAbierto = ref(false);
const empleadoAEditar = ref(null);
const isLoading = ref(true);

/**
 * Carga la lista de empleados desde la API.
 */
async function cargarEmpleados() {
  isLoading.value = true;
  try {
    empleados.value = await adminApi.obtenerEmpleados();
  } catch (error) {
    console.error('Error cargando empleados:', error.response?.data || error.message);
    // TODO: Implementar notificación global de error
  } finally {
    isLoading.value = false;
  }
}

/**
 * Retorna la clase de badge según el rol.
 */
function getBadgeClass(rol) {
  switch(rol.toLowerCase()) {
    case 'administrador': return 'badge bg-danger text-uppercase';
    case 'moderador': return 'badge bg-warning text-dark text-uppercase';
    case 'logistica': return 'badge bg-info text-dark text-uppercase';
    default: return 'badge bg-secondary text-uppercase';
  }
}

function abrirModalRegistro() { 
  modalRegistroAbierto.value = true; 
}

function cerrarModalRegistro() { 
  modalRegistroAbierto.value = false; 
}

function abrirModalActualizacion(empleado) {
  // Se recomienda incluir la lógica de ID faltante para prevenir errores.
  if (!empleado.id && !empleado.idUsuario) {
    console.error("Empleado sin ID: No se puede abrir el modal de edición.");
    return; 
  }

  empleadoAEditar.value = JSON.parse(JSON.stringify(empleado)); 
  modalActualizacionAbierto.value = true;
}

function cerrarModalActualizacion() { 
  modalActualizacionAbierto.value = false; 
  empleadoAEditar.value = null;
}

// Para que los modals recarguen la lista al completar una acción
function handleEmpleadoAccion() {
  cerrarModalRegistro();
  cerrarModalActualizacion();
  cargarEmpleados();
}

onMounted(() => {
  cargarEmpleados();
});
</script>

<style scoped>
/* Estilos específicos para la vista */
.badge {
  font-weight: 600;
  padding: 0.4em 0.8em;
}

</style>
